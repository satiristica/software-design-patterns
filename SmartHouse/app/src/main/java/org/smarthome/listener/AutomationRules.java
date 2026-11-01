package org.smarthome.listener;

import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;
import org.smarthome.observer.Subject;

import java.util.Objects;

public final class AutomationRules implements Observer<HomeEvent> {
    private final double powerLimitWatts;
    private final Subject<HomeEvent> subject;

    public AutomationRules(double powerLimitWatts, Subject<HomeEvent> subject) {
        if (!Double.isFinite(powerLimitWatts) || powerLimitWatts < 0) {
            throw new IllegalArgumentException("Invalid power limit");
        }
        this.powerLimitWatts = powerLimitWatts;
        this.subject = Objects.requireNonNull(subject);
    }

    @Override
    public void onEvent(HomeEvent event) {
        if (event.type() == EventType.ROOM_SUMMARY && event.value() > powerLimitWatts) {
            subject.publish(new HomeEvent(EventType.POWER_LIMIT_EXCEEDED,
                    event.roomId(), "automation", event.value()));
        }
    }
}
