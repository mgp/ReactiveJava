package reactivejava.schedulers;

import reactivejava.disposables.Disposable;
import rx.functions.Action0;

/**
 * Represents a serial queue of work items.
 */
public interface Scheduler {
    /**
     * Enqueues an action on the scheduler.
     *
     * When the work is executed depends on the scheduler in use.
     *
     * Optionally returns a disposable that can be used to cancel the work before it begins.
     */
    Disposable schedule(Action0 action);
}
