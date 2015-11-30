package reactivejava.schedulers;

import reactivejava.disposables.Disposable;
import rx.functions.Action0;

/**
 * A scheduler that performs all work synchronously.
 */
public final class ImmediateScheduler implements Scheduler {
    public static final ImmediateScheduler INSTANCE = new ImmediateScheduler();

    private ImmediateScheduler() {
    }

    @Override
    public Disposable schedule(Action0 action) {
        action.call();
        return null;
    }
}
