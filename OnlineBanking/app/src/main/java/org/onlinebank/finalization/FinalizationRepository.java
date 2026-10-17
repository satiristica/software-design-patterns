package org.onlinebank.finalization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FinalizationRepository {

    private final Path filePath;

    
    
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();



    public FinalizationRepository() {
        this(Path.of("data", "settlements.json"));
    }



    public FinalizationRepository(Path filePath) {
        this.filePath = filePath.toAbsolutePath();
    }



    public Settlement findByTransferId(String transferId) {
        return load().settlements.get(transferId);
    }

    

    public void save(Settlement settlement) {
        Data data = load();

        data.settlements.put(settlement.getTransferId(), settlement);

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, gson.toJson(data));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to save settlements", exception
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

            if (data == null || data.settlements == null) {
                throw new IllegalStateException(
                        "Invalid settlements file"
                );
            }

            return data;
        } catch (IOException | JsonParseException exception) {
            throw new IllegalStateException(
                    "Failed to load settlements", exception
            );
        }
    }


    private static class Data {
        Map<String, Settlement> settlements = new HashMap<>();
    }
}