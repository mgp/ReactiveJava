package reactivejava;

import com.google.common.base.Objects;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Atomic<V> {
    public interface Copyable<V> {
        V copy();
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private V value;

    public Atomic() {
        this(null);
    }

    public Atomic(V value) {
        this.value = value;
    }

    public V get() {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return value;
        } finally {
            readLock.unlock();
        }
    }

    public void set(V newValue) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            value = newValue;
        } finally {
            writeLock.unlock();
        }
    }

    public V swap(V newValue) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            final V prevValue = value;
            value = newValue;
            return prevValue;
        } finally {
            writeLock.unlock();
        }
    }

    public void modify(Action1<V> action) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            action.call(value);
        } finally {
            writeLock.unlock();
        }
    }

    public V modify(Func1<V, V> function) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            // TODO(mgp): Enforce this at
            if (value == null) {
                value = function.call(value);
                return null;
            } else if (value instanceof Copyable) {
                V oldValue = ((Copyable<V>) value).copy();
                value = function.call(value);
                return oldValue;
            } else {
                throw new UnsupportedOperationException("modify(Func) but type does not implement Copyable");
            }
        } finally {
            writeLock.unlock();
        }
    }
}
