package reactivejava.disposables;

import reactivejava.Bag;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link Disposable} that will dispose of any number of other {@link Disposable} instances.
 */
public final class CompositeDisposable implements Disposable {
    private final ReactiveJavaAtomicReference<Bag<Disposable>> disposables;

    /**
     * Represents a handle to a {@link Disposable} previously added to a {@link CompositeDisposable}.
     */
    public static final class DisposableHandle {
        private final AtomicReference<Bag.RemovalToken> bagToken;
        private final WeakReference<CompositeDisposable> disposable;

        private static final DisposableHandle EMPTY = new DisposableHandle();

        private DisposableHandle() {
            this.bagToken = new AtomicReference<>();
            this.disposable = new WeakReference<>(null);
        }

        private DisposableHandle(Bag.RemovalToken bagToken, CompositeDisposable disposable) {
            this.bagToken = new AtomicReference<>(bagToken);
            this.disposable = new WeakReference<>(disposable);
        }

        /**
         * Removes the pointed-to {@link Disposable} from its {@link CompositeDisposable}.
         *
         * This is useful to minimize memory growth, by removing {@link Disposable} instances that are no longer needed.
         */
        public void remove() {
            final Bag.RemovalToken removalToken = bagToken.getAndSet(null);
            final CompositeDisposable disposable = this.disposable.get();
            if ((removalToken != null) && (disposable != null)) {
                disposable.disposables.modify(new Action1<Bag<Disposable>>() {
                    @Override
                    public void call(Bag<Disposable> bag) {
                        bag.remove(removalToken);
                    }
                });
            }
        }
    }

    /**
     * Initializes an empty {@link CompositeDisposable}.
     */
    CompositeDisposable() {
        this(Collections.<Disposable>emptyList());
    }

    /**
     * Constructs a {@link CompositeDisposable} containing the given {@link Iterable} of {@link Disposable} instances.
     */
    CompositeDisposable(Iterable<Disposable> disposables) {
        final Bag<Disposable> bag = new Bag<>();

        for (Disposable disposable : disposables) {
            bag.insert(disposable);
        }

        this.disposables = new ReactiveJavaAtomicReference<>(bag);
    }

    @Override
    public boolean isDisposed() {
        return disposables.get() == null;
    }

    @Override
    public void dispose() {
        final Bag<Disposable> disposables = this.disposables.getAndSet(null);
        for (int i = disposables.size() - 1; i >= 0; --i) {
            disposables.get(i).dispose();
        }
    }

    /**
     * Adds the given {@link Disposable} to the list, then returns a {@link DisposableHandle} which can be used to
     * opaquely remove the disposable later (if desired).
     */
    public DisposableHandle add(final Disposable disposable) {
        if (disposable == null) {
            return DisposableHandle.EMPTY;
        }

        final DisposableHandle handle = disposables.modify(new Func1<Bag<Disposable>, DisposableHandle>() {
            @Override
            public DisposableHandle call(final Bag<Disposable> bag) {
                if (bag != null) {
                    final Bag.RemovalToken token = bag.insert(disposable);
                    return new DisposableHandle(token, CompositeDisposable.this);
                } else {
                    return null;
                }
            }
        });

        if (handle != null) {
            return handle;
        } else {
            disposable.dispose();
            return DisposableHandle.EMPTY;
        }
    }

    /**
     * Adds an {@link ActionDisposable} to the list.
     */
    public DisposableHandle add(final Action0 action) {
        return add(new ActionDisposable(action));
    }
}
