package org.smarthome.model;

import java.util.List;
import java.util.Objects;

public record Room(String id, List<HomeElement> devices) implements HomeElement {
    public Room {
        Objects.requireNonNull(id);
        devices = List.copyOf(devices);
    }
}
