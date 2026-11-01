package org.smarthome.visitor;

import org.junit.jupiter.api.Test;
import org.smarthome.model.AirFryer;
import org.smarthome.model.Home;
import org.smarthome.model.HomeElement;
import org.smarthome.model.Kettle;
import org.smarthome.model.Light;
import org.smarthome.model.Room;
import org.smarthome.model.SmartTV;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisitorTest {
    private final Kettle kettle = new Kettle("kettle-1", 1500);
    private final AirFryer airFryer = new AirFryer("fryer-1", 1800);
    private final Light light = new Light("light-1", 10);
    private final SmartTV tv = new SmartTV("tv-1", 100);
    private final Room kitchen = new Room("kitchen", List.of(kettle, airFryer, light));
    private final Room livingRoom = new Room("living-room", List.of(tv));
    private final Home home = new Home("home-1", List.of(kitchen, livingRoom));
    private final PowerVisitor powerVisitor = new PowerVisitor();

    @Test
    void calculatesHomePower() {
        assertEquals(3410.0, home.accept(powerVisitor), 0.000001);
    }

    @Test
    void calculatesOnlySelectedRoom() {
        assertEquals(3310.0, kitchen.accept(powerVisitor), 0.000001);
        assertEquals(100.0, livingRoom.accept(powerVisitor), 0.000001);
    }

    @Test
    void readsUpdatedDevicePower() {
        kettle.setPowerWatts(0);

        assertEquals(1910.0, home.accept(powerVisitor), 0.000001);
    }

    @Test
    void calculatesZeroForEmptyContainers() {
        assertEquals(0.0, new Home("empty-home", List.of()).accept(powerVisitor), 0.000001);
        assertEquals(0.0, new Room("empty-room", List.of()).accept(powerVisitor), 0.000001);
    }

    @Test
    void visitsAllDeviceTypesThroughHomeElement() {
        HomeElement[] devices = {kettle, airFryer, light, tv};
        double[] expected = {1500, 1800, 10, 100};

        for (int i = 0; i < devices.length; i++) {
            assertEquals(expected[i], devices[i].accept(powerVisitor), 0.000001);
        }
    }
}
