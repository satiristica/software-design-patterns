package org.smarthome.concurrency;

import org.junit.jupiter.api.Test;
import org.smarthome.observer.ConcurrentSubject;
import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {
    @Test
    void deliversExactly4000EventsFromFourThreads() throws Exception {
        ConcurrentSubject<HomeEvent> subject = new ConcurrentSubject<>();
        AtomicInteger received = new AtomicInteger();
        subject.subscribe(EventType.MOTION_DETECTED, event -> received.incrementAndGet());

        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> tasks = new ArrayList<>();

        try {
            for (int i = 0; i < 4; i++) {
                HomeEvent event = new HomeEvent(
                        EventType.MOTION_DETECTED, "room-1", "camera-" + i, 1);
                tasks.add(executor.submit(() -> {
                    start.await();
                    for (int j = 0; j < 1000; j++) {
                        subject.publish(event);
                    }
                    return null;
                }));
            }

            start.countDown();
            for (Future<?> task : tasks) {
                task.get(10, TimeUnit.SECONDS);
            }
        } finally {
            start.countDown();
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
            }
        }

        assertEquals(4000, received.get());
    }
}
