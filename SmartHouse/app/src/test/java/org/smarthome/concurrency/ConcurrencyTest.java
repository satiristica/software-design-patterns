package org.smarthome.concurrency;

import org.junit.jupiter.api.Test;
import org.smarthome.observer.EventType;
import org.smarthome.observer.HomeEvent;
import org.smarthome.observer.Observer;
import org.smarthome.observer.Subject;
import org.smarthome.source.EventSource;

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
        Subject<HomeEvent> subject = new EventSource();
        CountingObserver observer = new CountingObserver();
        subject.subscribe(EventType.DEVICE_STATE_CHANGED, observer);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> tasks = new ArrayList<>();

        try {
            for (int i = 0; i < 4; i++) {
                HomeEvent event = new HomeEvent(
                        EventType.DEVICE_STATE_CHANGED, "room-1", "kettle-" + i, 1500);
                tasks.add(executor.submit(new ProducerTask(subject, event, start)));
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

        assertEquals(4000, observer.received.get());
    }

    private static class CountingObserver implements Observer<HomeEvent> {
        private final AtomicInteger received = new AtomicInteger();

        @Override
        public void onEvent(HomeEvent event) {
            received.incrementAndGet();
        }
    }

    private static class ProducerTask implements Runnable {
        private final Subject<HomeEvent> subject;
        private final HomeEvent event;
        private final CountDownLatch start;

        private ProducerTask(Subject<HomeEvent> subject, HomeEvent event, CountDownLatch start) {
            this.subject = subject;
            this.event = event;
            this.start = start;
        }

        @Override
        public void run() {
            try {
                start.await();
                for (int i = 0; i < 1000; i++) {
                    subject.publish(event);
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(exception);
            }
        }
    }
}
