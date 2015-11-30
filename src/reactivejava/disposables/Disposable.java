package reactivejava.disposables;

/**
 * Represents something that can be “disposed,” usually associated with freeing resources or canceling work.
 */
public interface Disposable {
    /**
     * @return whether this {@link Disposable} has been disposed already
     */
    boolean isDisposed();

    void dispose();
}
