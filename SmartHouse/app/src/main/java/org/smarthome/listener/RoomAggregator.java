package org.smarthome.listener;

import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;
import org.smarthome.observer.Subject;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class RoomAggregator implements Observer<HomeEvent>, Subject<HomeEvent> {
    private final Subject<HomeEvent> subject;
    private final ConcurrentMap<String, TemperatureStats> temperatures = new ConcurrentHashMap<>();

    public RoomAggregator(Subject<HomeEvent> subject) {
        this.subject = Objects.requireNonNull(subject);
    }

    @Override
    public void onEvent(HomeEvent event) {
        if (event.type() != EventType.TEMPERATURE_CHANGED) {
            return;
        }

        TemperatureStats stats = temperatures.compute(event.roomId(), (roomId, previous) ->
                previous == null
                        ? new TemperatureStats(event.value(), 1)
                        : new TemperatureStats(previous.sum() + event.value(), previous.count() + 1));

        publish(new HomeEvent(EventType.ROOM_SUMMARY, event.roomId(),
                "room-aggregator", stats.sum() / stats.count()));
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

    private record TemperatureStats(double sum, long count) {
    }
}
