package reactivejava.schedulers;

import com.google.common.base.MoreObjects;
import reactivejava.disposables.ActionDisposable;
import reactivejava.disposables.Disposable;
import reactivejava.disposables.SerialDisposable;
import rx.functions.Action0;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A scheduler that implements virtualized time, for use in testing.
 */
public final class TestScheduler implements DateScheduler {
    private static final class ScheduledAction {
        private final long date;
        private final Action0 action;

        private ScheduledAction(long date, Action0 action) {
            this.date = date;
            this.action = checkNotNull(action);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("date", date)
                    .add("action", action)
                    .toString();
        }
    }

    private final ReentrantLock lock = new ReentrantLock();
    private final PriorityQueue<ScheduledAction> scheduledActions = new PriorityQueue<>(8, new Comparator<ScheduledAction>() {
        @Override
        public int compare(ScheduledAction lhs, ScheduledAction rhs) {
            if (lhs.date == rhs.date) {
                return 0;
            } else {
                return (lhs.date < rhs.date) ? -1 : 1;
            }
        }
    });

    private long currentDate;

    /**
     * Constructs a {@link TestScheduler} with the given start date.
     */
    public TestScheduler(long startDate) {
        this.currentDate = startDate;
    }

    @Override
    public long getCurrentDate() {
        lock.lock();
        try {
            return currentDate;
        } finally {
            lock.unlock();
        }
    }

    private Disposable schedule(final ScheduledAction scheduledAction) {
        lock.lock();
        try {
            scheduledActions.add(scheduledAction);
        } finally {
            lock.unlock();
        }

        return new ActionDisposable(new Action0() {
            @Override
            public void call() {
                lock.lock();
                for (Iterator<ScheduledAction> iterator = scheduledActions.iterator(); iterator.hasNext(); ) {
                    if (iterator.next() == scheduledAction) {
                        iterator.remove();
                        break;
                    }
                }
                lock.unlock();
            }
        });

    }

    @Override
    public Disposable schedule(final Action0 action) {
        return schedule(new ScheduledAction(currentDate, action));
    }

    /**
     * Schedules an action for execution at or after the given interval (counted from {@link #getCurrentDate()}).
     *
     * Optionally returns a disposable that can be used to cancel the work before it begins.
     */
    public Disposable scheduleAfterInterval(final long interval, final Action0 action) {
        return scheduleAfterDate(currentDate + interval, action);
    }

    @Override
    public Disposable scheduleAfterDate(final long date, final Action0 action) {
        return schedule(new ScheduledAction(date, action));
    }

    private void scheduleAfterDate(final long date,
                                   final Action0 action,
                                   final long repeatInterval,
                                   final SerialDisposable serialDisposable) {
        checkArgument(repeatInterval >= 0, "repeatInterval < 0: %s", repeatInterval);

        final Disposable disposable = scheduleAfterDate(date, new Action0() {
            @Override
            public void call() {
                action.call();
                scheduleAfterDate(date + repeatInterval, action, repeatInterval, serialDisposable);
            }
        });
        serialDisposable.setInnerDisposable(disposable);
    }

    /**
     * Schedules a recurring action at the given interval, beginning at the given interval (counted from
     * {@link #getCurrentDate()}).
     *
     * Optionally returns a disposable that can be used to cancel the work before it begins.
     */
    public Disposable scheduleAfterInterval(final long interval, final Action0 action, final long repeatInterval) {
        return scheduleAfterDate(currentDate + interval, action, repeatInterval);
    }

    @Override
    public Disposable scheduleAfterDate(final long date, final Action0 action, final long repeatInterval) {
        final SerialDisposable disposable = new SerialDisposable();
        scheduleAfterDate(date, action, repeatInterval, disposable);
        return disposable;
    }

    /**
     * Advances the virtualized clock by an extremely tiny interval, dequeuing and executing any actions along the way.
     *
     * This is intended to be used as a way to execute actions that have been scheduled to run as soon as possible.
     */
    public void advance() {
        advanceByInterval(1);
    }

    /**
     * Advances the virtualized clock by the given interval, dequeuing and executing any actions along the way.
     */
    public void advanceByInterval(long interval) {
        lock.lock();
        try {
            advanceToDate(currentDate + interval);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Advances the virtualized clock to the given future date, dequeuing and executing any actions up until that point.
     */
    public void advanceToDate(final long newDate) {
        lock.lock();

        try {
            checkArgument(currentDate < newDate, "newDate=%s, getCurrentDate=%s", newDate, currentDate);
            currentDate = newDate;

            while (!scheduledActions.isEmpty()) {
                if (newDate < scheduledActions.peek().date) {
                    break;
                }

                final ScheduledAction scheduledAction = scheduledActions.remove();
                scheduledAction.action.call();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Dequeues and executes all scheduled actions, leaving the scheduler's date at {@link Long#MAX_VALUE}.
     */
    public void run() {
        advanceToDate(Long.MAX_VALUE);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("scheduledActions", scheduledActions)
                .toString();
    }
}
