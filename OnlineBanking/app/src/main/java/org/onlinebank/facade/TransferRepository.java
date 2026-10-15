package org.onlinebank.facade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TransferRepository {

    private final Path filePath;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

            
    public TransferRepository() {
        this(Path.of("data", "transfers.json"));
    }


    public TransferRepository(Path filePath) {
        this.filePath = filePath.toAbsolutePath();
    }


    public void save(TransferProcess process) {
        Data data = load();
        data.transfers.put(process.getTransferId(), process);

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, gson.toJson(data));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save transfers", e);
        }
    }


    public TransferProcess findByTransferId(String transferId) {
        return load().transfers.get(transferId);
    }


    private Data load() {
        if (Files.notExists(filePath)) {
            return new Data();
        }

        try {
            String json = Files.readString(filePath);
            Data data = gson.fromJson(json, Data.class);

            if (data == null || data.transfers == null) {
                throw new IllegalStateException("Invalid transfers file");
            }

            return data;
        } catch (IOException | JsonParseException e) {
            throw new IllegalStateException("Failed to load transfers", e);
        }
    }


    private static class Data {
        private Map<String, TransferProcess> transfers = new HashMap<>();
    }
}