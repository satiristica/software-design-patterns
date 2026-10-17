package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onlinebank.intake.AccountsData;
import org.onlinebank.intake.BankAccount;
import org.onlinebank.intake.FundsHold;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountsDataTest {

    @TempDir
    Path directory;

    @Test
    void restoresAccountAfterReopening() throws IOException {
        Path file = directory.resolve("accounts.json");
        AccountsData data = new AccountsData(file);

        BankAccount account = accountWithBalance("100000");
        account.freeze();

        data.saveAccount(account);

        AccountsData reopened = new AccountsData(file);
        BankAccount restored = reopened.findAccount("ACC-001");

        assertEquals("John", restored.getOwnerName());
        assertEquals("KZT", restored.getCurrency());
        assertEquals(new BigDecimal("100000"), restored.getBalance());
        assertEquals(BankAccount.Status.FROZEN, restored.getStatus());
    }

    @Test
    void restoresAccountAndHoldAfterReopening() throws IOException {
        Path file = directory.resolve("accounts.json");
        AccountsData data = new AccountsData(file);

        BankAccount account = accountWithBalance("100000");
        account.withdraw(new BigDecimal("25000"));

        FundsHold hold = new FundsHold(
                "TR-001",
                "ACC-001",
                new BigDecimal("25000")
        );
        hold.place();

        data.save(account, hold);

        AccountsData reopened = new AccountsData(file);
        BankAccount restoredAccount = reopened.findAccount("ACC-001");
        FundsHold restoredHold = reopened.findHold("TR-001");

        assertEquals(
                new BigDecimal("75000"),
                restoredAccount.getBalance()
        );

        assertEquals("TR-001", restoredHold.getTransferId());
        assertEquals("ACC-001", restoredHold.getAccountId());
        assertEquals(new BigDecimal("25000"), restoredHold.getAmount());
        assertEquals(FundsHold.Status.HELD, restoredHold.getStatus());
    }

    @Test
    void savesUpdatedBalance() throws IOException {
        Path file = directory.resolve("accounts.json");
        AccountsData data = new AccountsData(file);

        BankAccount account = accountWithBalance("100000");
        data.saveAccount(account);

        account.deposit(new BigDecimal("50000"));
        data.saveAccount(account);

        AccountsData reopened = new AccountsData(file);

        assertEquals(
                new BigDecimal("150000"),
                reopened.findAccount("ACC-001").getBalance()
        );
    }

    private BankAccount accountWithBalance(String balance) {
        return new BankAccount(
                "ACC-001",
                "John",
                "KZT",
                new BigDecimal(balance)
        );
    }
}
