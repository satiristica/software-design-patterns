package org.smarthome.visitor;

import org.smarthome.model.AirFryer;
import org.smarthome.model.CoffeeMachine;
import org.smarthome.model.Home;
import org.smarthome.model.Kettle;
import org.smarthome.model.Light;
import org.smarthome.model.Room;
import org.smarthome.model.SmartTV;

public interface Visitor<R> {
    R visit(Home home);

    R visit(Room room);

    R visit(Kettle kettle);

    R visit(AirFryer airFryer);

    R visit(Light light);

    R visit(SmartTV tv);

    R visit(CoffeeMachine coffeeMachine);
}
