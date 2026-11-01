package org.smarthome.visitor;

import org.junit.jupiter.api.Test;
import org.smarthome.model.AirFryer;
import org.smarthome.model.CoffeeMachine;
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
    private final CoffeeMachine coffeeMachine = new CoffeeMachine("coffee-1", 1200);
    private final Room kitchen = new Room("kitchen", List.of(kettle, airFryer, light, coffeeMachine));
    private final Room livingRoom = new Room("living-room", List.of(tv));
    private final Home home = new Home("home-1", List.of(kitchen, livingRoom));
    private final PowerVisitor powerVisitor = new PowerVisitor();

    @Test
    void calculatesHomeAndRoomPower() {
        assertEquals(4610.0, home.accept(powerVisitor), 0.000001);
        assertEquals(4510.0, kitchen.accept(powerVisitor), 0.000001);
        assertEquals(100.0, livingRoom.accept(powerVisitor), 0.000001);

        kettle.setPowerWatts(0);
        assertEquals(3110.0, home.accept(powerVisitor), 0.000001);
        coffeeMachine.setPowerWatts(0);
        assertEquals(1910.0, home.accept(powerVisitor), 0.000001);
    }

    @Test
    void accumulatesStatisticsAcrossVisits() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        home.accept(visitor);
        assertEquals(2, visitor.roomCount());
        assertEquals(5, visitor.deviceCount());

        kitchen.accept(visitor);
        assertEquals(3, visitor.roomCount());
        assertEquals(9, visitor.deviceCount());
    }

    @Test
    void filtersDevicesWithinRequestedSubtree() {
        FilterVisitor visitor = new FilterVisitor(1000);

        assertEquals(List.of(kettle, airFryer, coffeeMachine), home.accept(visitor));
        assertTrue(livingRoom.accept(visitor).isEmpty());
        assertEquals(List.of(kettle, airFryer, light, coffeeMachine, tv), home.accept(new FilterVisitor(0)));
    }

    @Test
    void updatesFilterResultsAndExcludesThresholdBoundary() {
        FilterVisitor visitor = new FilterVisitor(100);
        List<HomeElement> previous = home.accept(visitor);
        assertEquals(List.of(kettle, airFryer, coffeeMachine), previous);

        kettle.setPowerWatts(0);
        coffeeMachine.setPowerWatts(100);
        assertEquals(List.of(airFryer), home.accept(visitor));
        assertEquals(List.of(kettle, airFryer, coffeeMachine), previous);
    }

    @Test
    void handlesEmptyContainersAndNoMatches() {
        Home emptyHome = new Home("empty-home", List.of());
        Room emptyRoom = new Room("empty-room", List.of());
        assertEquals(0.0, emptyHome.accept(powerVisitor), 0.000001);
        assertEquals(0.0, emptyRoom.accept(powerVisitor), 0.000001);

        StatisticsVisitor statistics = new StatisticsVisitor();
        emptyHome.accept(statistics);
        assertEquals(0, statistics.roomCount());
        assertEquals(0, statistics.deviceCount());
        emptyRoom.accept(statistics);
        assertEquals(1, statistics.roomCount());
        assertEquals(0, statistics.deviceCount());

        FilterVisitor filter = new FilterVisitor(5000);
        assertTrue(home.accept(filter).isEmpty());
        assertTrue(emptyHome.accept(filter).isEmpty());
        assertTrue(emptyRoom.accept(filter).isEmpty());

        JsonVisitor json = new JsonVisitor();
        assertEquals("{\"type\":\"Home\",\"id\":\"empty-home\",\"rooms\":[]}", emptyHome.accept(json));
        assertEquals("{\"type\":\"Room\",\"id\":\"empty-room\",\"devices\":[]}", emptyRoom.accept(json));
    }

    @Test
    void exportsNestedJsonAndEscapesIds() {
        JsonVisitor visitor = new JsonVisitor();
        String expected = "{\"type\":\"Home\",\"id\":\"home-1\",\"rooms\":["
                + "{\"type\":\"Room\",\"id\":\"kitchen\",\"devices\":["
                + "{\"type\":\"Kettle\",\"id\":\"kettle-1\",\"powerWatts\":1500.0},"
                + "{\"type\":\"AirFryer\",\"id\":\"fryer-1\",\"powerWatts\":1800.0},"
                + "{\"type\":\"Light\",\"id\":\"light-1\",\"powerWatts\":10.0},"
                + "{\"type\":\"CoffeeMachine\",\"id\":\"coffee-1\",\"powerWatts\":1200.0}]},"
                + "{\"type\":\"Room\",\"id\":\"living-room\",\"devices\":["
                + "{\"type\":\"SmartTV\",\"id\":\"tv-1\",\"powerWatts\":100.0}]}]}";
        assertEquals(expected, home.accept(visitor));

        Kettle special = new Kettle("\"\\\n" + (char) 1, 0);
        String escaped = "\\\"" + "\\\\" + "\\u000a" + "\\u0001";
        assertEquals("{\"type\":\"Kettle\",\"id\":\"" + escaped + "\",\"powerWatts\":0.0}",
                special.accept(visitor));
    }
}
