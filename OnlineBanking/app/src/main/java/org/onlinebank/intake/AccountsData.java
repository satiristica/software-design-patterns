package org.onlinebank.intake;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsData {

    private final Path filePath;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();


            
    public AccountsData() {
        this(Path.of("data", "accounts.json"));
    }



    public AccountsData(Path filePath) {
        this.filePath = filePath.toAbsolutePath();
    }



    public BankAccount findAccount(String accountId) {
        return load().accounts.get(accountId);
    }



    public FundsHold findHold(String transferId) {
        return load().holds.get(transferId);
    }



    public List<BankAccount> getAccounts() {
        return List.copyOf(load().accounts.values());
    }



    public List<FundsHold> getHolds() {
        return List.copyOf(load().holds.values());
    }



    public void saveAccount(BankAccount account) {
        Data data = load();

        data.accounts.put(account.getAccountId(), account);

        saveData(data);
    }



    public void save(BankAccount account, FundsHold hold) {
        if (!account.getAccountId().equals(hold.getAccountId())) {
            throw new IllegalArgumentException(
                    "Hold belongs to another account"
            );
        }

        Data data = load();

        data.accounts.put(account.getAccountId(), account);
        data.holds.put(hold.getTransferId(), hold);

        saveData(data);
    }



    private Data load() {
        
        if (Files.notExists(filePath)) {
            return new Data();
        }

        try {
            String json = Files.readString(filePath);
            Data data = gson.fromJson(json, Data.class);

            if (data == null || data.accounts == null || data.holds == null) {
                throw new IllegalStateException("Invalid accounts file");
            }

            return data;

        } catch (IOException | JsonParseException e) {
            throw new IllegalStateException(
                    "Failed to load accounts", e
            );
        }
    }



    private void saveData(Data data) {
        try {
            Files.createDirectories(filePath.getParent());

            Files.writeString(filePath, gson.toJson(data));

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to save accounts", e
            );
        }
    }


    private static class Data {
        Map<String, BankAccount> accounts = new HashMap<>();
        Map<String, FundsHold> holds = new HashMap<>();
    }
}