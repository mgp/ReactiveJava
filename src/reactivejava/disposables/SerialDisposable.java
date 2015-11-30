package reactivejava.disposables;

import reactivejava.Atomic;
import rx.functions.Func1;

/**
 * A {@link Disposable} that will optionally dispose of another {@link Disposable}.
 */
public final class SerialDisposable implements Disposable {
    private static final class State implements Atomic.Copyable<State> {
        private Disposable innerDisposable;
        private boolean disposed;

        private State() {
            this(null, false);
        }

        private State(Disposable innerDisposable, boolean disposed) {
            this.innerDisposable = innerDisposable;
            this.disposed = disposed;
        }

        private void disposeInnerDisposable() {
            if (innerDisposable != null) {
                innerDisposable.dispose();
            }
        }

        public State copy() {
            return new State(innerDisposable, disposed);
        }
    }

    private final ReactiveJavaAtomicReference<State> state = new ReactiveJavaAtomicReference<>(new State());

    public SerialDisposable() {
        this(null);
    }

    /**
     * Initializes the receiver to dispose of the argument when the {@link SerialDisposable} is disposed.
     */
    public SerialDisposable(final Disposable disposable) {
        setInnerDisposable(disposable);
    }

    /**
     * @return the inner disposable to dispose of
     */
    public Disposable getInnerDisposable() {
        return state.get().innerDisposable;
    }

    /**
     * Sets the inner disposable to dispose of.
     *
     * Whenever this value is set (even to the same value!), the previous disposable is automatically disposed.
     */
    public void setInnerDisposable(final Disposable disposable) {
        final State prevState = state.modify(new Func1<State, State>() {
            @Override
            public State call(final State state) {
                state.innerDisposable = disposable;
                return state;
            }
        });

        prevState.disposeInnerDisposable();
        if (prevState.disposed) {
            disposable.dispose();
        }
    }

    @Override
    public boolean isDisposed() {
        return state.get().disposed;
    }

    @Override
    public void dispose() {
        State prevState = state.getAndSet(new State(null, true));
        if (prevState != null) {
            prevState.disposeInnerDisposable();
        }
    }
}
