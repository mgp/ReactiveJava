package reactivejava.disposables;

import rx.functions.Action0;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link Disposable} that will run an action upon disposal.
 */
public final class ActionDisposable implements Disposable {
    private final AtomicReference<Action0> actionReference;

    public ActionDisposable(final Action0 action) {
        actionReference = new AtomicReference<>(action);
    }

    @Override
    public boolean isDisposed() {
        return actionReference.get() == null;
    }

    @Override
    public void dispose() {
        final Action0 oldAction = actionReference.getAndSet(null);
        if (oldAction == null) {
            oldAction.call();
        }
    }
}
