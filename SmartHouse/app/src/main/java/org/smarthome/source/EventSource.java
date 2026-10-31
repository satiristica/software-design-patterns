package org.smarthome.source;

import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;
import org.smarthome.observer.Subject;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventSource implements Subject<HomeEvent> {
    private final Map<EventType, CopyOnWriteArrayList<Observer<HomeEvent>>> observers =
            new EnumMap<>(EventType.class);

    public EventSource() {
        for (EventType type : EventType.values()) {
            observers.put(type, new CopyOnWriteArrayList<>());
        }
    }

    @Override
    public void subscribe(EventType type, Observer<HomeEvent> observer) {
        observers.get(type).addIfAbsent(Objects.requireNonNull(observer));
    }

    @Override
    public void unsubscribe(EventType type, Observer<HomeEvent> observer) {
        observers.get(type).remove(observer);
    }

    @Override
    public void publish(HomeEvent event) {
        for (Observer<HomeEvent> observer : observers.get(event.type())) {
            observer.onEvent(event);
        }
    }
}
