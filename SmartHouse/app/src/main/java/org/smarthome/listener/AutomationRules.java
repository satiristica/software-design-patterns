package org.smarthome.listener;

import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;
import org.smarthome.observer.Subject;

import java.util.Objects;

public final class AutomationRules implements Observer<HomeEvent> {
    private final double targetTemperature;
    private final double heatingPowerWatts;
    private final Subject<HomeEvent> subject;

    public AutomationRules(double targetTemperature, double heatingPowerWatts,
                           Subject<HomeEvent> subject) {
        if (!Double.isFinite(targetTemperature) || !Double.isFinite(heatingPowerWatts)
                || heatingPowerWatts < 0) {
            throw new IllegalArgumentException("Invalid heating settings");
        }
        this.targetTemperature = targetTemperature;
        this.heatingPowerWatts = heatingPowerWatts;
        this.subject = Objects.requireNonNull(subject);
    }

    @Override
    public void onEvent(HomeEvent event) {
        if (event.type() == EventType.TEMPERATURE_CHANGED) {
            double power = event.value() < targetTemperature ? heatingPowerWatts : 0;
            subject.publish(new HomeEvent(EventType.DEVICE_STATE_CHANGED,
                    event.roomId(), event.sourceId(), power));
        }
    }
}
