package org.smarthome.listener;

import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;

import java.io.PrintStream;
import java.util.Objects;

public final class DashboardListener implements Observer<HomeEvent> {
    private final PrintStream output;

    public DashboardListener(PrintStream output) {
        this.output = Objects.requireNonNull(output);
    }

    @Override
    public void onEvent(HomeEvent event) {
        output.printf("[%s] room=%s source=%s value=%s%n",
                event.type(), event.roomId(), event.sourceId(), event.value());
    }
}
