package org.smarthome.observer;

import java.util.Objects;

public record HomeEvent(
        EventType type,
        String roomId,
        String sourceId,
        double value
) implements Event {
    public HomeEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(roomId);
        Objects.requireNonNull(sourceId);
    }
}
