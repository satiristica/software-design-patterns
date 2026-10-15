package org.onlinebank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onlinebank.finalization.AuditLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuditLoggerTest {

    @TempDir
    Path directory;


    @Test
    void savesEventToJson() throws IOException {
        Path file = directory.resolve("audit.json");
        AuditLogger logger = new AuditLogger(file);

        logger.log("TR-001", "Settlement confirmed");

        JsonArray events = readEvents(file);
        assertEquals(1, events.size());

        JsonObject event = events.get(0).getAsJsonObject();

        assertEquals("TR-001", event.get("transferId").getAsString());
        assertEquals(
                "Settlement confirmed",
                event.get("message").getAsString()
        );
        assertFalse(event.get("timestamp").getAsString().isBlank());
    }


    @Test
    void preservesPreviousEventsAfterReopening() throws IOException {
        Path file = directory.resolve("audit.json");
        AuditLogger logger = new AuditLogger(file);

        logger.log("TR-001", "Settlement confirmed");

        AuditLogger reopened = new AuditLogger(file);
        reopened.log("TR-002", "Settlement failed");

        JsonArray events = readEvents(file);

        assertEquals(2, events.size());
        assertEquals(
                "TR-001",
                events.get(0).getAsJsonObject()
                        .get("transferId").getAsString()
        );
        assertEquals(
                "TR-002",
                events.get(1).getAsJsonObject()
                        .get("transferId").getAsString()
        );
    }


    @Test
    void rejectsEmptyMessage() {
        Path file = directory.resolve("audit.json");
        AuditLogger logger = new AuditLogger(file);

        assertThrows(
                IllegalArgumentException.class,
                () -> logger.log("TR-001", "")
        );

        assertFalse(Files.exists(file));
    }

    
    private JsonArray readEvents(Path file) throws IOException {
        return JsonParser.parseString(
                Files.readString(file)
        ).getAsJsonArray();
    }
}