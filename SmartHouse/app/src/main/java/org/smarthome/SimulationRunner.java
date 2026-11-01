package org.smarthome;

import org.smarthome.model.HomeElement;
import org.smarthome.model.Kettle;
import org.smarthome.model.SmartTV;
import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class SimulationRunner {
    private final ExecutorService executor;
    private final List<Runnable> producers;

    public SimulationRunner(ExecutorService executor, List<Runnable> producers) {
        this.executor = Objects.requireNonNull(executor);
        this.producers = List.copyOf(producers);
        if (this.producers.size() < 3) {
            throw new IllegalArgumentException("At least three producers are required");
        }
    }

    public void run() throws InterruptedException, ExecutionException {
        List<Future<?>> tasks = new ArrayList<>();
        try {
            for (Runnable producer : producers) {
                tasks.add(executor.submit(producer));
            }
            executor.shutdown();
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Producers did not finish in time");
            }
            for (Future<?> task : tasks) {
                task.get();
            }
        } finally {
            if (!executor.isTerminated()) {
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Producer threads did not stop");
                }
            }
        }
    }

    public static final class ProducerTask implements Runnable {
        private final String roomId;
        private final HomeElement device;
        private final double activePowerWatts;
        private final int eventCount;
        private final Subject<HomeEvent> source;

        public ProducerTask(String roomId, HomeElement device, double activePowerWatts,
                            int eventCount, Subject<HomeEvent> source) {
            this.roomId = Objects.requireNonNull(roomId);
            this.device = Objects.requireNonNull(device);
            this.source = Objects.requireNonNull(source);
            if (!(device instanceof Kettle) && !(device instanceof SmartTV)) {
                throw new IllegalArgumentException("Producer requires a kettle or TV");
            }
            if (!Double.isFinite(activePowerWatts) || activePowerWatts < 0 || eventCount < 1) {
                throw new IllegalArgumentException("Invalid producer settings");
            }
            this.activePowerWatts = activePowerWatts;
            this.eventCount = eventCount;
        }

        @Override
        public void run() {
            for (int i = 0; i < eventCount; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new IllegalStateException("Producer interrupted");
                }
                double power = i % 2 == 0 ? activePowerWatts : 0;
                if (device instanceof Kettle kettle) {
                    kettle.setPowerWatts(power);
                } else if (device instanceof SmartTV tv) {
                    tv.setPowerWatts(power);
                }
                source.publish(new HomeEvent(EventType.DEVICE_STATE_CHANGED,
                        roomId, device.id(), power));
            }
        }
    }
}
