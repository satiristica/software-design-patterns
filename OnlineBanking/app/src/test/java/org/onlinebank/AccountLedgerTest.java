package org.onlinebank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onlinebank.exceptions.InsufficientFundsException;
import org.onlinebank.intake.AccountLedger;
import org.onlinebank.intake.AccountsData;
import org.onlinebank.intake.BankAccount;
import org.onlinebank.intake.CurrencyExchange;
import org.onlinebank.intake.FundsHold;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountLedgerTest {

    @TempDir
    Path directory;

    
    private AccountsData data;
    private AccountLedger ledger;


    @BeforeEach
    void setUp() {
        data = new AccountsData(directory.resolve("accounts.json"));
        ledger = new AccountLedger(data, new CurrencyExchange());


        BankAccount account = new BankAccount(
                "ACC-001",
                "John",
                "DEMO-IBAN",
                "KZT",
                new BigDecimal("100000")
        );

        data.saveAccount(account);
    }


    @Test
    void placingHoldReservesMoney() {
        ledger.placeHold("TR-001", "ACC-001", new BigDecimal("25000"));

        assertBalance("75000");
        assertEquals(
                FundsHold.Status.HELD,
                data.findHold("TR-001").getStatus()
        );
        assertEquals(
                new BigDecimal("25000"),
                data.findHold("TR-001").getAmount()
        );
    }


    @Test
    void capturingHoldDoesNotWithdrawAgain() {
        ledger.placeHold("TR-001", "ACC-001", new BigDecimal("25000"));

        ledger.captureHold("TR-001");

        assertBalance("75000");
        assertEquals(
                FundsHold.Status.CAPTURED,
                data.findHold("TR-001").getStatus()
        );
    }

    
    @Test
    void releasingHoldReturnsMoney() {
        ledger.placeHold("TR-001", "ACC-001", new BigDecimal("25000"));

        ledger.releaseHold("TR-001");

        assertBalance("100000");
        assertEquals(
                FundsHold.Status.RELEASED,
                data.findHold("TR-001").getStatus()
        );
    }


    @Test
    void insufficientFundsDoesNotCreateHold() {
        assertThrows(
                InsufficientFundsException.class,
                () -> ledger.placeHold(
                        "TR-001",
                        "ACC-001",
                        new BigDecimal("150000")
                )
        );

        assertBalance("100000");
        assertNull(data.findHold("TR-001"));
    }


    @Test
    void releasingHoldTwiceDoesNotReturnMoneyTwice() {
        ledger.placeHold("TR-001", "ACC-001", new BigDecimal("25000"));
        ledger.releaseHold("TR-001");

        ledger.releaseHold("TR-001");

        assertBalance("100000");
        assertEquals(
                FundsHold.Status.RELEASED,
                data.findHold("TR-001").getStatus()
        );
    }

    private void assertBalance(String expected) {
        assertEquals(
                new BigDecimal(expected),
                data.findAccount("ACC-001").getBalance()
        );
    }
}
