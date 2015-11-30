package reactivejava;

import reactivejava.events.*;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * An Observer is a simple wrapper around a function which can receive {@link Event} instances (typically on a
 * {@link Signal}.
 */
public final class Observer<V> {
    private final Action1<Event<V>> action;

    public Observer(final Action1<Event<V>> action) {
        this.action = checkNotNull(action);
    }

    public static final class Builder<V> {
        private Action1<V> onNextHandler;
        private Action1<Throwable> onFailedHandler;
        private Action0 onInterruptedHandler;
        private Action0 onCompletedHandler;

        public void setOnNextHandler(final Action1<V> onNextHandler) {
            checkState(this.onNextHandler == null);
            checkNotNull(onNextHandler);
            this.onNextHandler = onNextHandler;
        }

        public void setOnFailedHandler(final Action1<Throwable> onFailedHandler) {
            checkState(this.onFailedHandler == null);
            checkNotNull(onFailedHandler);
            this.onFailedHandler = onFailedHandler;
        }

        public void setOnInterruptedHandler(final Action0 onInterruptedHandler) {
            checkState(this.onInterruptedHandler == null);
            checkNotNull(onInterruptedHandler);
            this.onInterruptedHandler = onInterruptedHandler;
        }

        public void setOnCompletedHandler(final Action0 onCompletedHandler) {
            checkState(this.onCompletedHandler == null);
            checkNotNull(onCompletedHandler);
            this.onCompletedHandler = onCompletedHandler;
        }

        public Observer<V> build() {
            final Action1<V> onNextHandler = this.onNextHandler;
            final Action1<Throwable> onErrorHandler = this.onFailedHandler;
            final Action0 onInterruptedHandler = this.onInterruptedHandler;
            final Action0 onCompletedHandler = this.onCompletedHandler;

            final Action1<Event<V>> action = new Action1<Event<V>>() {
                @Override
                public void call(Event<V> event) {
                    switch (event.getType()) {
                        case NEXT:
                            if (onNextHandler != null) {
                                OnNextEvent<V> onNextEvent = (OnNextEvent<V>) event;
                                onNextHandler.call(onNextEvent.value);
                            }
                            break;
                        case FAILED:
                            if (onErrorHandler != null) {
                                OnFailedEvent<V> onFailedEvent = (OnFailedEvent<V>) event;
                                onErrorHandler.call(onFailedEvent.error);
                            }
                            break;
                        case COMPLETED:
                            if (onCompletedHandler != null) {
                                onCompletedHandler.call();
                            }
                            break;
                        case INTERRUPTED:
                            if (onInterruptedHandler != null) {
                                onInterruptedHandler.call();
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unrecognized type: " + event.getType());
                    }
                }
            };

            return new Observer<>(action);
        }
    }

    /**
     * Puts a {@link OnNextEvent} into this observer.
     */
    public void sendNext(V value) {
        action.call(Event.forOnNext(value));
    }

    /**
     * Puts a {@link OnFailedEvent} into this observer.
     */
    public void sendFailed(Throwable error) {
        action.call(Event.<V>forOnFailed(error));
    }

    /**
     * Puts a {@link OnCompletedEvent} into this observer.
     */
    public void sendCompleted() {
        action.call(Event.<V>forOnCompleteted());
    }

    /**
     * Puts an {@link OnInterruptedEvent} into this observer.
     */
    public void sendInterrupted() {
        action.call(Event.<V>forOnInterrupted());
    }
}
