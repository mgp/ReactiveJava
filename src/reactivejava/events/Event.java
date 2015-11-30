package reactivejava.events;

import rx.functions.Func1;

/**
 * Represents a signal event.
 *
 * Signals must conform to the grammar:
 * {@code Next* (Failed | Completed | Interrupted)?}
 */
public abstract class Event<V> {
    public enum Type {
        /**
         * A value provided by the signal.
         */
        NEXT,

        /**
         * The signal terminated because of an error. No further events will be received.
         */
        FAILED,

        /**
         * The signal successfully terminated. No further events will be received.
         */
        COMPLETED,

        /**
         * Event production on the signal has been interrupted. No further events will be received.
         */
        INTERRUPTED
    }

    /**
     * @return an {@link OnNextEvent} instance configured with the given value
     */
    public static <V> OnNextEvent<V> forOnNext(final V value) {
        return new OnNextEvent<>(value);
    }

    /**
     * @return an {@link OnFailedEvent} instance configured with the given error
     */
    public static <V> OnFailedEvent<V> forOnFailed(final Throwable error) {
        return new OnFailedEvent<>(error);
    }

    /**
     * @return an {@link OnInterruptedEvent} instance
     */
    public static <V> OnInterruptedEvent<V> forOnInterrupted() {
        return (OnInterruptedEvent<V>) OnInterruptedEvent.INSTANCE;
    }

    /**
     * @return an {@link OnCompletedEvent} instance
     */
    public static <V> OnCompletedEvent<V> forOnCompleteted() {
        return (OnCompletedEvent<V>) OnCompletedEvent.INSTANCE;
    }

    Event() {
    }

    /**
     * @return the {@link Type} of this event
     */
    public abstract Type getType();

    /**
     * @return whether this event indicates signal termination (i.e., that no further events will be received)
     */
    public final boolean isTerminating() {
        switch (getType()) {
            case NEXT:
                return false;
            case FAILED:
            case COMPLETED:
            case INTERRUPTED:
                return true;
            default:
                throw new IllegalStateException("Unrecognized type: " + getType());
        }
    }

    /**
     * Lifts the given function over the event's value.
     */
    public final <U> Event<U> map(Func1<V, U> function) {
        switch (getType()) {
            case NEXT:
                final OnNextEvent<V> onNextEvent = (OnNextEvent<V>) this;
                return Event.forOnNext(function.call(onNextEvent.value));
            case FAILED:
                final OnFailedEvent<V> onFailedEvent = (OnFailedEvent<V>) this;
                return Event.forOnFailed(onFailedEvent.error);
            case COMPLETED:
                return Event.forOnCompleteted();
            case INTERRUPTED:
                return Event.forOnInterrupted();
            default:
                throw new IllegalStateException("Unrecognized type: " + getType());
        }
    }

    /**
     * Lifts the given function over the event's error.
     */
    public final Event<V> mapError(Func1<Throwable, Throwable> function) {
        switch (getType()) {
            case NEXT:
                final OnNextEvent<V> onNextEvent = (OnNextEvent<V>) this;
                return Event.forOnNext(onNextEvent.value);
            case FAILED:
                final OnFailedEvent<V> onFailedEvent = (OnFailedEvent<V>) this;
                return Event.forOnFailed(function.call(onFailedEvent.error));
            case COMPLETED:
                return Event.forOnCompleteted();
            case INTERRUPTED:
                return Event.forOnInterrupted();
            default:
                throw new IllegalStateException("Unrecognized type: " + getType());
        }
    }

    /**
     * @return the contained value if {@link #getType()} returns {@link Type#NEXT}
     */
    public V getValue() {
        switch (getType()) {
            case NEXT:
                final OnNextEvent<V> onNextEvent = (OnNextEvent<V>) this;
                return onNextEvent.value;
            default:
                return null;
        }
    }

    /**
     * @return the contained error value if {@link #getType()} returns {@link Type#FAILED}
     */
    public Throwable getError() {
        switch (getType()) {
            case FAILED:
                final OnFailedEvent<V> onFailedEvent = (OnFailedEvent<V>) this;
                return onFailedEvent.error;
            default:
                return null;
        }
    }
}
