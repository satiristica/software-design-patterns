package org.onlinebank.finalization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public class AuditLogger {

    private final Path filePath;

    
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

            
    public AuditLogger() {
        this(Path.of("data", "audit.json"));
    }


    public AuditLogger(Path filePath) {
        this.filePath = filePath.toAbsolutePath();
    }


    public void log(String transferId, String message) {
        if (transferId == null || transferId.isBlank()
                || message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Transfer ID and message are required"
            );
        }

        JsonArray events = load();

        JsonObject event = new JsonObject();
        event.addProperty("transferId", transferId);
        event.addProperty("message", message);
        event.addProperty("timestamp", Instant.now().toString());

        events.add(event);

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, gson.toJson(events));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to save audit log", exception
            );
        }
    }


    private JsonArray load() {
        if (Files.notExists(filePath)) {
            return new JsonArray();
        }

        try {
            JsonArray events = gson.fromJson(
                    Files.readString(filePath),
                    JsonArray.class
            );

            if (events == null) {
                throw new IllegalStateException("Invalid audit file");
            }

            return events;
        } catch (IOException | JsonParseException exception) {
            throw new IllegalStateException(
                    "Failed to load audit log", exception
            );
        }
    }
}