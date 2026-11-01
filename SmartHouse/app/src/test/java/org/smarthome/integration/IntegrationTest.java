package org.smarthome.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smarthome.listener.AutomationRules;
import org.smarthome.listener.EnergyTracker;
import org.smarthome.listener.RoomAggregator;
import org.smarthome.model.AirFryer;
import org.smarthome.model.Home;
import org.smarthome.model.Kettle;
import org.smarthome.model.Room;
import org.smarthome.model.SmartTV;
import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;
import org.smarthome.source.EventSource;
import org.smarthome.visitor.FilterVisitor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {
    private final EventSource kettleSource = new EventSource();
    private final EventSource tvSource = new EventSource();
    private final EventSource reports = new EventSource();
    private final EnergyTracker tracker = new EnergyTracker();
    private final RoomAggregator aggregator = new RoomAggregator(reports);
    private final AutomationRules rules = new AutomationRules(2000, reports);
    private final RecordingObserver summaries = new RecordingObserver();
    private final RecordingObserver alerts = new RecordingObserver();

    @BeforeEach
    void connectListeners() {
        for (EventSource source : List.of(kettleSource, tvSource)) {
            source.subscribe(EventType.DEVICE_STATE_CHANGED, tracker);
            source.subscribe(EventType.DEVICE_STATE_CHANGED, aggregator);
        }
        aggregator.subscribe(EventType.ROOM_SUMMARY, summaries);
        aggregator.subscribe(EventType.ROOM_SUMMARY, rules);
        reports.subscribe(EventType.POWER_LIMIT_EXCEEDED, alerts);
    }

    @Test
    void combinesTwoSourcesIntoRoomPower() {
        kettleSource.publish(new HomeEvent(
                EventType.DEVICE_STATE_CHANGED, "kitchen", "kettle-1", 1500));
        tvSource.publish(new HomeEvent(
                EventType.DEVICE_STATE_CHANGED, "kitchen", "tv-1", 100));

        assertEquals(1600, tracker.totalPowerWatts());
        assertEquals(2, summaries.received.size());
        assertEquals("kitchen", summaries.received.get(1).roomId());
        assertEquals(1600, summaries.received.get(1).value());
        assertTrue(alerts.received.isEmpty());
    }

    @Test
    void auditsOnlyOverloadedRoomAndUpdatesAfterSwitchOff() {
        Kettle kettle = new Kettle("kettle-1", 1900);
        SmartTV tv = new SmartTV("tv-1", 100);
        Room kitchen = new Room("kitchen", List.of(kettle, tv));
        Room livingRoom = new Room("living-room", List.of(new AirFryer("fryer-1", 5000)));
        Home home = new Home("home-1", List.of(kitchen, livingRoom));
        RecordingObserver audits = new RecordingObserver();
        SecurityAuditListener audit = new SecurityAuditListener(home, new FilterVisitor(1000), reports);
        reports.subscribe(EventType.POWER_LIMIT_EXCEEDED, audit);
        reports.subscribe(EventType.SECURITY_AUDITED, audits);

        kettleSource.publish(new HomeEvent(
                EventType.DEVICE_STATE_CHANGED, "kitchen", "kettle-1", 1900));
        tvSource.publish(new HomeEvent(
                EventType.DEVICE_STATE_CHANGED, "kitchen", "tv-1", 100));
        assertEquals(2000, summaries.received.get(1).value());
        assertTrue(alerts.received.isEmpty());
        assertTrue(audits.received.isEmpty());

        tv.setPowerWatts(200);
        tvSource.publish(new HomeEvent(
                EventType.DEVICE_STATE_CHANGED, "kitchen", "tv-1", 200));
        assertEquals(1, alerts.received.size());
        HomeEvent alert = alerts.received.get(0);
        assertEquals(EventType.POWER_LIMIT_EXCEEDED, alert.type());
        assertEquals("kitchen", alert.roomId());
        assertEquals(2100, alert.value());
        assertEquals(1, audits.received.size());
        HomeEvent report = audits.received.get(0);
        assertEquals(EventType.SECURITY_AUDITED, report.type());
        assertEquals("kitchen", report.roomId());
        assertEquals(1, report.value());

        kettle.setPowerWatts(0);
        kettleSource.publish(new HomeEvent(
                EventType.DEVICE_STATE_CHANGED, "kitchen", "kettle-1", 0));
        assertEquals(200, tracker.totalPowerWatts());
        assertEquals(200, summaries.received.get(3).value());
        assertEquals(1, alerts.received.size());
        assertEquals(1, audits.received.size());
    }

    @Test
    void keepsRoomPowerSeparate() {
        kettleSource.publish(new HomeEvent(
                EventType.DEVICE_STATE_CHANGED, "kitchen", "kettle-1", 1900));
        tvSource.publish(new HomeEvent(
                EventType.DEVICE_STATE_CHANGED, "living-room", "tv-1", 300));

        assertEquals(2200, tracker.totalPowerWatts());
        assertEquals("kitchen", summaries.received.get(0).roomId());
        assertEquals(1900, summaries.received.get(0).value());
        assertEquals("living-room", summaries.received.get(1).roomId());
        assertEquals(300, summaries.received.get(1).value());
        assertTrue(alerts.received.isEmpty());
    }

    private static class RecordingObserver implements Observer<HomeEvent> {
        private final List<HomeEvent> received = new ArrayList<>();

        @Override
        public void onEvent(HomeEvent event) {
            received.add(event);
        }
    }
}
