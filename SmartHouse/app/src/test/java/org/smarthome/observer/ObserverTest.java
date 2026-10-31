package org.smarthome.observer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObserverTest {
    private final Subject<HomeEvent> subject = new ConcurrentSubject<>();
    private final List<HomeEvent> received = new ArrayList<>();
    private final HomeEvent motion =
            new HomeEvent(EventType.MOTION_DETECTED, "room-1", "camera-1", 1);
    private final HomeEvent temperature =
            new HomeEvent(EventType.TEMPERATURE_CHANGED, "room-1", "thermostat-1", 22);

    @Test
    void deliversEvent() {
        subject.subscribe(EventType.MOTION_DETECTED, received::add);

        subject.publish(motion);

        assertEquals(1, received.size());
        assertSame(motion, received.get(0));
    }

    @Test
    void ignoresOtherEventTypes() {
        subject.subscribe(EventType.MOTION_DETECTED, received::add);

        subject.publish(temperature);

        assertTrue(received.isEmpty());
    }

    @Test
    void notifiesAllListeners() {
        List<HomeEvent> otherReceived = new ArrayList<>();
        subject.subscribe(EventType.MOTION_DETECTED, received::add);
        subject.subscribe(EventType.MOTION_DETECTED, otherReceived::add);

        subject.publish(motion);

        assertEquals(List.of(motion), received);
        assertEquals(List.of(motion), otherReceived);
    }

    @Test
    void preventsDuplicateSubscriptions() {
        Observer<HomeEvent> observer = received::add;
        subject.subscribe(EventType.MOTION_DETECTED, observer);
        subject.subscribe(EventType.MOTION_DETECTED, observer);

        subject.publish(motion);

        assertEquals(List.of(motion), received);
    }

    @Test
    void stopsDeliveryAfterUnsubscribe() {
        Observer<HomeEvent> observer = received::add;
        subject.subscribe(EventType.MOTION_DETECTED, observer);
        subject.unsubscribe(EventType.MOTION_DETECTED, observer);

        subject.publish(motion);

        assertTrue(received.isEmpty());
    }

    @Test
    void allowsUnsubscribeInsideHandler() {
        Observer<HomeEvent> observer = new Observer<HomeEvent>() {
            @Override
            public void onEvent(HomeEvent event) {
                received.add(event);
                subject.unsubscribe(event.type(), this);
            }
        };
        subject.subscribe(EventType.MOTION_DETECTED, observer);

        subject.publish(motion);
        subject.publish(motion);

        assertEquals(List.of(motion), received);
    }

    @Test
    void subscriptionInsideHandlerStartsWithNextEvent() {
        Observer<HomeEvent> added = received::add;
        subject.subscribe(EventType.MOTION_DETECTED,
                event -> subject.subscribe(EventType.MOTION_DETECTED, added));

        subject.publish(motion);
        assertTrue(received.isEmpty());

        subject.publish(motion);
        assertEquals(List.of(motion), received);
    }

    @Test
    void unsubscribeKeepsOtherEventSubscriptions() {
        Observer<HomeEvent> observer = received::add;
        subject.subscribe(EventType.MOTION_DETECTED, observer);
        subject.subscribe(EventType.TEMPERATURE_CHANGED, observer);
        subject.unsubscribe(EventType.MOTION_DETECTED, observer);

        subject.publish(motion);
        subject.publish(temperature);

        assertEquals(List.of(temperature), received);
    }
}
