package org.smarthome.visitor;

import org.smarthome.model.AirFryer;
import org.smarthome.model.CoffeeMachine;
import org.smarthome.model.Home;
import org.smarthome.model.HomeElement;
import org.smarthome.model.Kettle;
import org.smarthome.model.Light;
import org.smarthome.model.Room;
import org.smarthome.model.SmartTV;

import java.util.StringJoiner;

public final class JsonVisitor implements Visitor<String> {
    @Override
    public String visit(Home home) {
        StringJoiner rooms = new StringJoiner(",", "[", "]");
        for (Room room : home.rooms()) {
            rooms.add(room.accept(this));
        }
        return "{\"type\":\"Home\",\"id\":" + quote(home.id()) + ",\"rooms\":" + rooms + "}";
    }

    @Override
    public String visit(Room room) {
        StringJoiner devices = new StringJoiner(",", "[", "]");
        for (HomeElement device : room.devices()) {
            devices.add(device.accept(this));
        }
        return "{\"type\":\"Room\",\"id\":" + quote(room.id()) + ",\"devices\":" + devices + "}";
    }

    @Override
    public String visit(Kettle kettle) {
        return device("Kettle", kettle.id(), kettle.powerWatts());
    }

    @Override
    public String visit(AirFryer airFryer) {
        return device("AirFryer", airFryer.id(), airFryer.powerWatts());
    }

    @Override
    public String visit(Light light) {
        return device("Light", light.id(), light.powerWatts());
    }

    @Override
    public String visit(SmartTV tv) {
        return device("SmartTV", tv.id(), tv.powerWatts());
    }

    @Override
    public String visit(CoffeeMachine coffeeMachine) {
        return device("CoffeeMachine", coffeeMachine.id(), coffeeMachine.powerWatts());
    }

    private String device(String type, String id, double powerWatts) {
        return "{\"type\":" + quote(type) + ",\"id\":" + quote(id)
                + ",\"powerWatts\":" + powerWatts + "}";
    }

    private String quote(String value) {
        StringBuilder result = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character == '"' || character == '\\') {
                result.append('\\').append(character);
            } else if (character < 0x20) {
                result.append(String.format("\\u%04x", (int) character));
            } else {
                result.append(character);
            }
        }
        return result.append('"').toString();
    }
}
