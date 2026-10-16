package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.onlinebank.finalization.NotificationService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationServiceTest {

    private final List<String> messages = new ArrayList<>();

    private final NotificationService service =
            new NotificationService(messages::add);

    @Test
    void sendsTransferNotification() {
        service.send("TR-001", "Completed successfully");

        assertEquals(
                List.of("Transfer TR-001: Completed successfully"),
                messages
        );
    }

    @Test
    void rejectsMissingTransferId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.send("", "Completed")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.send(null, "Completed")
        );

        assertTrue(messages.isEmpty());
    }

    @Test
    void rejectsMissingMessage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.send("TR-001", "")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.send("TR-001", null)
        );

        assertTrue(messages.isEmpty());
    }
}