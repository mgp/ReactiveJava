package reactivejava.events;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * An {@link Event} with type {@link Type#NEXT}. To construct such an instance, call
 * {@link Events#forOnNext(Object)}.
 *
 * @param <V> the type of emitted values on the corresponding signal
 */
public final class OnNextEvent<V> extends Event<V> {
    public final V value;

    OnNextEvent(final V value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.NEXT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof OnNextEvent)) {
            return false;
        }

        final OnNextEvent<?> rhs = (OnNextEvent<?>) obj;
        return Objects.equal(value, rhs.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getType(), value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .toString();
    }
}
