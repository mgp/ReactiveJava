package reactivejava.events;

import com.google.common.base.MoreObjects;

/**
 * An {@link Event} with type {@link Type#COMPLETED}. To construct such an instance, call
 * {@link Events#forOnCompleteted()}.
 *
 * @param <V> the type of emitted values on the corresponding signal
 */
public final class OnCompletedEvent<V> extends Event<V> {
    public static final OnCompletedEvent<?> INSTANCE = new OnCompletedEvent<>();

    @Override
    public Type getType() {
        return Type.COMPLETED;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).toString();
    }
}
