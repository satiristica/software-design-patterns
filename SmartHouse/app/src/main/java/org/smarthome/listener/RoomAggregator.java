package org.smarthome.listener;

import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;
import org.smarthome.observer.Subject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class RoomAggregator implements Observer<HomeEvent>, Subject<HomeEvent> {
    private final Subject<HomeEvent> subject;
    private final Map<String, HomeEvent> latestEvents = new HashMap<>();

    public RoomAggregator(Subject<HomeEvent> subject) {
        this.subject = Objects.requireNonNull(subject);
    }

    @Override
    public void onEvent(HomeEvent event) {
        if (event.type() != EventType.DEVICE_STATE_CHANGED) {
            return;
        }

        double total = 0;
        synchronized (latestEvents) {
            latestEvents.put(event.sourceId(), event);
            for (HomeEvent latest : latestEvents.values()) {
                if (latest.roomId().equals(event.roomId())) {
                    total += latest.value();
                }
            }
        }
        publish(new HomeEvent(EventType.ROOM_SUMMARY, event.roomId(), "room-aggregator", total));
    }

    @Override
    public void subscribe(EventType type, Observer<HomeEvent> observer) {
        subject.subscribe(type, observer);
    }

    @Override
    public void unsubscribe(EventType type, Observer<HomeEvent> observer) {
        subject.unsubscribe(type, observer);
    }

    @Override
    public void publish(HomeEvent event) {
        subject.publish(event);
    }
}
