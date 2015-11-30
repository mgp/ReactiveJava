package reactivejava.disposables;

/**
 * A {@link Disposable} that only flips {@link #isDisposed()} upon disposal, and performs no other work.
 */
public final class SimpleDisposable implements Disposable {
    private volatile boolean disposed = false;

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public void dispose() {
        disposed = true;
    }
}
