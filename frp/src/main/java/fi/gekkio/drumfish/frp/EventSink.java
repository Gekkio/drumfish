package fi.gekkio.drumfish.frp;

import javax.annotation.Nullable;

/**
 * Target for events.
 * 
 * @param <T>
 *            event type
 */
public interface EventSink<T> {

    /**
     * Publishes a new event to the sink.
     * 
     * @param event
     */
    void fire(@Nullable T event);

}
