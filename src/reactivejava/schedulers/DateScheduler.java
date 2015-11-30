package reactivejava.schedulers;

import reactivejava.disposables.Disposable;
import rx.functions.Action0;

/**
 * A particular kind of {@link Scheduler} that supports enqueuing actions at future dates.
 */
public interface DateScheduler extends Scheduler {
    /**
     * The current date, as determined by this scheduler.
     *
     * This can be implemented to deterministic return a known date (e.g., for testing purposes).
     */
    long getCurrentDate();

    /**
     * Schedules an action for execution at or after the given date.
     *
     * Optionally returns a disposable that can be used to cancel the work before it begins.
     */
    Disposable scheduleAfterDate(long date, Action0 action);

    /**
     * Schedules a recurring action at the given interval, beginning at the given start time.
     *
     * Optionally returns a disposable that can be used to cancel the work before it begins.
     */
    Disposable scheduleAfterDate(long date, Action0 action, long repeatInterval);
}
