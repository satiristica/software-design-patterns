package org.smarthome.source;

import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Subject;

import java.util.Objects;

public final class CameraSource {
    private final String roomId;
    private final String cameraId;
    private final Subject<HomeEvent> subject;

    public CameraSource(String roomId, String cameraId, Subject<HomeEvent> subject) {
        this.roomId = Objects.requireNonNull(roomId);
        this.cameraId = Objects.requireNonNull(cameraId);
        this.subject = Objects.requireNonNull(subject);
    }

    public void detectMotion() {
        subject.publish(new HomeEvent(EventType.MOTION_DETECTED, roomId, cameraId, 1));
    }
}
