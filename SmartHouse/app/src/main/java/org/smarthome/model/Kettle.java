package org.smarthome.model;

import org.smarthome.visitor.Visitor;

import java.util.Objects;

public final class Kettle implements HomeElement {
    private final String id;
    private volatile double powerWatts;

    public Kettle(String id, double powerWatts) {
        this.id = Objects.requireNonNull(id);
        setPowerWatts(powerWatts);
    }

    @Override
    public String id() {
        return id;
    }

    public double powerWatts() {
        return powerWatts;
    }

    public void setPowerWatts(double powerWatts) {
        if (!Double.isFinite(powerWatts) || powerWatts < 0) {
            throw new IllegalArgumentException("Invalid power");
        }
        this.powerWatts = powerWatts;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
