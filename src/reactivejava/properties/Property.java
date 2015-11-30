package reactivejava.properties;

import reactivejava.SignalProducer;

/**
 * Represents a property that allows observation of its changes.
 */
public interface Property<V> {
    /**
     * @return the current value of the property
     */
    V getValue();

    /**
     * @return a producer for Signals that will send the property's current value, followed by all changes over time
     */
    SignalProducer<V> getProducer();
}
