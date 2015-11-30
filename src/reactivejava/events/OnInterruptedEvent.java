package reactivejava.events;

import com.google.common.base.MoreObjects;

/**
 * An {@link Event} with type {@link Type#INTERRUPTED}. To construct such an instance, call
 * {@link Events#forOnInterrupted()}.
 *
 * @param <V> the type of emitted values on the corresponding signal
 */
public final class OnInterruptedEvent<V> extends Event<V> {
    public static final OnInterruptedEvent<?> INSTANCE = new OnInterruptedEvent<>();

    @Override
    public Type getType() {
        return Type.INTERRUPTED;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).toString();
    }
}
