package org.smarthome.observer;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ConcurrentSubject<E extends Event> implements Subject<E> {
    private final Map<EventType, CopyOnWriteArrayList<Observer<E>>> observers =
            new EnumMap<>(EventType.class);

    public ConcurrentSubject() {
        for (EventType type : EventType.values()) {
            observers.put(type, new CopyOnWriteArrayList<>());
        }
    }

    @Override
    public void subscribe(EventType type, Observer<E> observer) {
        observers.get(type).addIfAbsent(Objects.requireNonNull(observer));
    }

    @Override
    public void unsubscribe(EventType type, Observer<E> observer) {
        observers.get(type).remove(observer);
    }

    @Override
    public void publish(E event) {
        for (Observer<E> observer : observers.get(event.type())) {
            observer.onEvent(event);
        }
    }
}
