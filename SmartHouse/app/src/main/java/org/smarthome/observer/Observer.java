package org.smarthome.observer;

public interface Observer<E extends Event> {
    void onEvent(E event);
}
