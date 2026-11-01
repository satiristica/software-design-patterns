package org.smarthome.visitor;

import org.smarthome.model.AirFryer;
import org.smarthome.model.CoffeeMachine;
import org.smarthome.model.Home;
import org.smarthome.model.HomeElement;
import org.smarthome.model.Kettle;
import org.smarthome.model.Light;
import org.smarthome.model.Room;
import org.smarthome.model.SmartTV;

public final class PowerVisitor implements Visitor<Double> {
    @Override
    public Double visit(Home home) {
        double total = 0;
        for (Room room : home.rooms()) {
            total += room.accept(this);
        }
        return total;
    }

    @Override
    public Double visit(Room room) {
        double total = 0;
        for (HomeElement device : room.devices()) {
            total += device.accept(this);
        }
        return total;
    }

    @Override
    public Double visit(Kettle kettle) {
        return kettle.powerWatts();
    }

    @Override
    public Double visit(AirFryer airFryer) {
        return airFryer.powerWatts();
    }

    @Override
    public Double visit(Light light) {
        return light.powerWatts();
    }

    @Override
    public Double visit(SmartTV tv) {
        return tv.powerWatts();
    }

    @Override
    public Double visit(CoffeeMachine coffeeMachine) {
        return coffeeMachine.powerWatts();
    }
}
