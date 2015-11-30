package reactivejava.events;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * An {@link Event} with type {@link Type#FAILED}. To construct such an instance, call
 * {@link Events#forOnFailed(Throwable)}.
 *
 * @param <V> the type of emitted values on the corresponding signal
 */
public final class OnFailedEvent<V> extends Event<V> {
    public final Throwable error;

    OnFailedEvent(final Throwable error) {
        this.error = error;
    }

    @Override
    public Type getType() {
        return Type.FAILED;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof OnFailedEvent)) {
            return false;
        }

        final OnFailedEvent<?> rhs = (OnFailedEvent<?>) obj;
        return Objects.equal(error, rhs.error);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getType(), error);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("error", error)
                .toString();
    }
}
