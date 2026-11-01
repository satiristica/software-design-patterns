package org.smarthome.visitor;

import org.smarthome.model.AirFryer;
import org.smarthome.model.Home;
import org.smarthome.model.HomeElement;
import org.smarthome.model.Kettle;
import org.smarthome.model.Light;
import org.smarthome.model.Room;
import org.smarthome.model.SmartTV;

import java.util.concurrent.atomic.AtomicInteger;

public final class StatisticsVisitor implements Visitor<Void> {
    private final AtomicInteger rooms = new AtomicInteger();
    private final AtomicInteger devices = new AtomicInteger();

    @Override
    public Void visit(Home home) {
        for (Room room : home.rooms()) {
            room.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Room room) {
        rooms.incrementAndGet();
        for (HomeElement device : room.devices()) {
            device.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Kettle kettle) {
        devices.incrementAndGet();
        return null;
    }

    @Override
    public Void visit(AirFryer airFryer) {
        devices.incrementAndGet();
        return null;
    }

    @Override
    public Void visit(Light light) {
        devices.incrementAndGet();
        return null;
    }

    @Override
    public Void visit(SmartTV tv) {
        devices.incrementAndGet();
        return null;
    }

    public int roomCount() {
        return rooms.get();
    }

    public int deviceCount() {
        return devices.get();
    }
}
