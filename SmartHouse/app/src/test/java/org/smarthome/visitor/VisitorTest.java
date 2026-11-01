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
    void calculatesHomeAndRoomPower() {
        assertEquals(3410.0, home.accept(powerVisitor), 0.000001);
        assertEquals(3310.0, kitchen.accept(powerVisitor), 0.000001);
        assertEquals(100.0, livingRoom.accept(powerVisitor), 0.000001);
    }

    @Test
    void readsUpdatedDevicePower() {
        kettle.setPowerWatts(0);

        assertEquals(1910.0, home.accept(powerVisitor), 0.000001);
    }



    @Test
    void accumulatesStatisticsAcrossVisits() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        home.accept(visitor);
        assertEquals(2, visitor.roomCount());
        assertEquals(4, visitor.deviceCount());

        kitchen.accept(visitor);
        assertEquals(3, visitor.roomCount());
        assertEquals(7, visitor.deviceCount());
    }

    @Test
    void filtersDevicesWithinRequestedSubtree() {
        FilterVisitor visitor = new FilterVisitor(1000);

        assertEquals(List.of(kettle, airFryer), home.accept(visitor));
        assertTrue(livingRoom.accept(visitor).isEmpty());
        assertEquals(List.of(kettle, airFryer, light, tv), home.accept(new FilterVisitor(0)));
    }

    @Test
    void updatesFilterResultsAndExcludesThresholdBoundary() {
        FilterVisitor visitor = new FilterVisitor(100);
        List<HomeElement> previous = home.accept(visitor);
        assertEquals(List.of(kettle, airFryer), previous);

        kettle.setPowerWatts(0);
        assertEquals(List.of(airFryer), home.accept(visitor));
        assertEquals(List.of(kettle, airFryer), previous);
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
    }

}
