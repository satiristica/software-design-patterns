package org.onlinebank.processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ProcessingRepository {

    private final Path filePath;

    
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();


    public ProcessingRepository() {
        this(Path.of("data", "risk-checks.json"));
    }


    public ProcessingRepository(Path filePath) {
        this.filePath = filePath.toAbsolutePath();
    }


    public RiskCheck findByTransferId(String transferId) {
        return load().checks.get(transferId);
    }


    public void save(RiskCheck check) {
        Data data = load();

        data.checks.put(check.getTransferId(), check);

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, gson.toJson(data));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to save risk checks", exception
            );
        }
    }


    private Data load() {
        if (Files.notExists(filePath)) {
            return new Data();
        }

        try {
            String json = Files.readString(filePath);
            Data data = gson.fromJson(json, Data.class);

            if (data == null || data.checks == null) {
                throw new IllegalStateException("Invalid risk checks file");
            }

            return data;
        } catch (IOException | JsonParseException exception) {
            throw new IllegalStateException(
                    "Failed to load risk checks", exception
            );
        }
    }


    private static class Data {
        Map<String, RiskCheck> checks = new HashMap<>();
    }
}