package org.smarthome.model;

import org.smarthome.visitor.Visitor;

import java.util.List;
import java.util.Objects;

public record Home(String id, List<Room> rooms) implements HomeElement {
    public Home {
        Objects.requireNonNull(id);
        rooms = List.copyOf(rooms);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
