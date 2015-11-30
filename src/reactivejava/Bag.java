package reactivejava;

import java.util.*;

/**
 * An unordered, non-unique collection of values of type {@code E}.
 *
 * @param <E> the element type
 */
public final class Bag<E> implements Iterable<E> {
    /**
     * A uniquely identifying token for removing a value that was inserted into a {@link Bag}.
     */
    public static final class RemovalToken {
        private Integer identifier;

        private RemovalToken(final int identifier) {
            this.identifier = identifier;
        }
    }

    private static final class BagElement<E> {
        final E value;
        final RemovalToken token;

        int identifier;

        BagElement(final E value, final RemovalToken token, final int identifier) {
            this.value = value;
            this.token = token;
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return String.format("BagElement(%s)", value);
        }
    }

    private final List<BagElement<E>> elements = new ArrayList<>();
    private int currentIdentifier = 0;

    /**
     * Inserts the given value in the collection, and returns a token that can later be passed to
     * {@link #remove(RemovalToken)}.
     */
    public RemovalToken insert(final E value) {
        final int nextIdentier = currentIdentifier + 1;
        if (nextIdentier < currentIdentifier) {
            reindex();
        }

        final RemovalToken token = new RemovalToken(currentIdentifier);
        final BagElement element = new BagElement(value, token, currentIdentifier);

        elements.add(element);
        currentIdentifier++;

        return token;
    }

    /**
     * Removes a value, given the token returned from insert().
     *
     * If the value has already been removed, nothing happens.
     */
    public void remove(final RemovalToken token) {
        if (token.identifier != null) {
            final Integer identifier = token.identifier;
            // Removal is more likely for recent objects than old ones.
            final ListIterator<BagElement<E>> iterator = elements.listIterator(elements.size());
            while (iterator.hasPrevious()) {
                final BagElement<E> bagElement = iterator.previous();
                if (identifier.equals(bagElement.identifier)) {
                    iterator.remove();
                    token.identifier = null;
                    break;
                }
            }
        }
    }

    /**
     * In the event of an identifier overflow (highly, highly unlikely), this will reset all current identifiers to
     * reclaim a contiguous set of available identifiers for the future.
     */
    private void reindex() {
        final int size = elements.size();
        for (int i = 0; i < size; ++i) {
            currentIdentifier = i;

            final BagElement<E> bagElement = elements.get(i);
            bagElement.identifier = currentIdentifier;
            bagElement.token.identifier = currentIdentifier;
        }
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<BagElement<E>> unmodifiableIterator = Collections.unmodifiableList(elements).iterator();
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return unmodifiableIterator.hasNext();
            }
            @Override
            public E next() {
                return unmodifiableIterator.next().value;
            }
            @Override
            public void remove() {
                unmodifiableIterator.remove();
            }
        };
    }

    /**
     * @return whether this bag is empty
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * @return the size, or number of contained elements, in this bag
     */
    public int size() {
        return elements.size();
    }

    /**
     * @return the element at the given index in the bag
     */
    public E get(final int index) {
        return elements.get(index).value;
    }
}
