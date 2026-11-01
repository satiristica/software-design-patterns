package org.smarthome.integration;

import org.smarthome.model.Home;
import org.smarthome.model.HomeElement;
import org.smarthome.model.Room;
import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;
import org.smarthome.observer.Subject;
import org.smarthome.visitor.Visitor;

import java.util.List;
import java.util.Objects;

public final class SecurityAuditListener implements Observer<HomeEvent> {
    private final Home home;
    private final Visitor<List<HomeElement>> visitor;
    private final Subject<HomeEvent> subject;

    public SecurityAuditListener(Home home, Visitor<List<HomeElement>> visitor,
                                 Subject<HomeEvent> subject) {
        this.home = Objects.requireNonNull(home);
        this.visitor = Objects.requireNonNull(visitor);
        this.subject = Objects.requireNonNull(subject);
    }

    @Override
    public void onEvent(HomeEvent event) {
        if (event.type() != EventType.POWER_LIMIT_EXCEEDED) {
            return;
        }
        for (Room room : home.rooms()) {
            if (room.id().equals(event.roomId())) {
                List<HomeElement> findings = room.accept(visitor);
                subject.publish(new HomeEvent(EventType.SECURITY_AUDITED,
                        room.id(), "security-audit", findings.size()));
                return;
            }
        }
    }
}
