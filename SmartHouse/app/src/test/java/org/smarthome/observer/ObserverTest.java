package org.smarthome.observer;

import org.junit.jupiter.api.Test;
import org.smarthome.source.EventSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObserverTest {
    private final Subject<HomeEvent> subject = new EventSource();
    private final RecordingObserver observer = new RecordingObserver();
    private final HomeEvent deviceEvent =
            new HomeEvent(EventType.DEVICE_STATE_CHANGED, "room-1", "kettle-1", 1500);
    private final HomeEvent summary =
            new HomeEvent(EventType.ROOM_SUMMARY, "room-1", "room-aggregator", 1500);

    @Test
    void deliversEvent() {
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);

        subject.publish(deviceEvent);

        assertEquals(1, observer.received.size());
        assertSame(deviceEvent, observer.received.get(0));
    }

    @Test
    void ignoresOtherEventTypes() {
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);

        subject.publish(summary);

        assertTrue(observer.received.isEmpty());
    }

    @Test
    void notifiesAllListeners() {
        RecordingObserver other = new RecordingObserver();
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, other);

        subject.publish(deviceEvent);

        assertEquals(List.of(deviceEvent), observer.received);
        assertEquals(List.of(deviceEvent), other.received);
    }

    @Test
    void preventsDuplicateSubscriptions() {
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);

        subject.publish(deviceEvent);

        assertEquals(List.of(deviceEvent), observer.received);
    }

    @Test
    void stopsDeliveryAfterUnsubscribe() {
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);
        subject.unsubscribe(EventType.DEVICE_STATE_CHANGED, observer);

        subject.publish(deviceEvent);

        assertTrue(observer.received.isEmpty());
    }

    @Test
    void allowsUnsubscribeInsideHandler() {
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, new Observer<HomeEvent>() {
            @Override
            public void onEvent(HomeEvent event) {
                observer.onEvent(event);
                subject.unsubscribe(event.type(), this);
            }
        });

        subject.publish(deviceEvent);
        subject.publish(deviceEvent);

        assertEquals(List.of(deviceEvent), observer.received);
    }

    @Test
    void subscriptionInsideHandlerStartsWithNextEvent() {
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, new Observer<HomeEvent>() {
            @Override
            public void onEvent(HomeEvent event) {
                subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);
            }
        });

        subject.publish(deviceEvent);
        assertTrue(observer.received.isEmpty());

        subject.publish(deviceEvent);
        assertEquals(List.of(deviceEvent), observer.received);
    }

    @Test
    void unsubscribeKeepsOtherEventSubscriptions() {
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);
        subject.subscribe(EventType.ROOM_SUMMARY, observer);
        subject.unsubscribe(EventType.DEVICE_STATE_CHANGED, observer);

        subject.publish(deviceEvent);
        subject.publish(summary);

        assertEquals(List.of(summary), observer.received);
    }

    @Test
    void keepsEventSourcesIndependent() {
        Subject<HomeEvent> otherSource = new EventSource();
        RecordingObserver other = new RecordingObserver();
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);
        otherSource.subscribe(EventType.DEVICE_STATE_CHANGED, other);

        subject.publish(deviceEvent);
        assertEquals(List.of(deviceEvent), observer.received);
        assertTrue(other.received.isEmpty());

        HomeEvent tvEvent = new HomeEvent(
                EventType.DEVICE_STATE_CHANGED, "room-2", "tv-1", 100);
        otherSource.publish(tvEvent);
        assertEquals(List.of(deviceEvent), observer.received);
        assertEquals(List.of(tvEvent), other.received);
    }

    private static class RecordingObserver implements Observer<HomeEvent> {
        private final List<HomeEvent> received = new ArrayList<>();

        @Override
        public void onEvent(HomeEvent event) {
            received.add(event);
        }
    }
}
