package org.smarthome.visitor;

import org.smarthome.model.AirFryer;
import org.smarthome.model.Home;
import org.smarthome.model.HomeElement;
import org.smarthome.model.Kettle;
import org.smarthome.model.Light;
import org.smarthome.model.Room;
import org.smarthome.model.SmartTV;

import java.util.ArrayList;
import java.util.List;

public final class FilterVisitor implements Visitor<List<HomeElement>> {
    private final double powerThresholdWatts;

    public FilterVisitor(double powerThresholdWatts) {
        if (!Double.isFinite(powerThresholdWatts) || powerThresholdWatts < 0) {
            throw new IllegalArgumentException("Invalid power threshold");
        }
        this.powerThresholdWatts = powerThresholdWatts;
    }

    @Override
    public List<HomeElement> visit(Home home) {
        List<HomeElement> result = new ArrayList<>();
        for (Room room : home.rooms()) {
            result.addAll(room.accept(this));
        }
        return List.copyOf(result);
    }

    @Override
    public List<HomeElement> visit(Room room) {
        List<HomeElement> result = new ArrayList<>();
        for (HomeElement device : room.devices()) {
            result.addAll(device.accept(this));
        }
        return List.copyOf(result);
    }

    @Override
    public List<HomeElement> visit(Kettle kettle) {
        return match(kettle, kettle.powerWatts());
    }

    @Override
    public List<HomeElement> visit(AirFryer airFryer) {
        return match(airFryer, airFryer.powerWatts());
    }

    @Override
    public List<HomeElement> visit(Light light) {
        return match(light, light.powerWatts());
    }

    @Override
    public List<HomeElement> visit(SmartTV tv) {
        return match(tv, tv.powerWatts());
    }

    private List<HomeElement> match(HomeElement device, double powerWatts) {
        if (powerWatts > powerThresholdWatts) {
            return List.of(device);
        }
        return List.of();
    }
}
