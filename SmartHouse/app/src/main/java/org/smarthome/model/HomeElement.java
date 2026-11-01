package org.smarthome.model;

import org.smarthome.visitor.Visitor;

public interface HomeElement {
    String id();

    <R> R accept(Visitor<R> visitor);
}
