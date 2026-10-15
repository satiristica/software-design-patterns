package org.onlinebank.finalization;

import java.util.Objects;
import java.util.function.Consumer;

public class NotificationService {

    private final Consumer<String> output;

    public NotificationService() {
        this(System.out::println);
    }

    public NotificationService(Consumer<String> output) {
        this.output = Objects.requireNonNull(output);
    }

    public void send(String transferId, String message) {
        if (transferId == null || transferId.isBlank()
                || message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Transfer ID and message are required"
            );
        }

        output.accept("Transfer " + transferId + ": " + message);
    }
}