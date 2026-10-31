package org.smarthome.source;

import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Subject;

import java.util.Objects;

public final class ThermostatSource {
    private final String roomId;
    private final String thermostatId;
    private final Subject<HomeEvent> subject;

    public ThermostatSource(String roomId, String thermostatId, Subject<HomeEvent> subject) {
        this.roomId = Objects.requireNonNull(roomId);
        this.thermostatId = Objects.requireNonNull(thermostatId);
        this.subject = Objects.requireNonNull(subject);
    }

    public void changeTemperature(double temperature) {
        subject.publish(new HomeEvent(
                EventType.TEMPERATURE_CHANGED, roomId, thermostatId, temperature));
    }
}
