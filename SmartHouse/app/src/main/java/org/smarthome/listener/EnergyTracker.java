package org.smarthome.listener;

import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;

import java.util.HashMap;
import java.util.Map;

public final class EnergyTracker implements Observer<HomeEvent> {
    private final Map<String, Double> powerByDevice = new HashMap<>();

    @Override
    public synchronized void onEvent(HomeEvent event) {
        if (event.type() == EventType.DEVICE_STATE_CHANGED) {
            powerByDevice.put(event.sourceId(), event.value());
        }
    }

    public synchronized double totalPowerWatts() {
        double total = 0;
        for (double power : powerByDevice.values()) {
            total += power;
        }
        return total;
    }
}
