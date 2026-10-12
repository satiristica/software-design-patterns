package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.onlinebank.intake.BankAccount;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankAccountTest {

    @Test
    void withdrawReducesBalance() {
        BankAccount account = new BankAccount(
                "ACC-001",
                "John",
                "DEMO-IBAN",
                "KZT",
                new BigDecimal("100000")
        );

        account.withdraw(new BigDecimal("50000"));

        assertEquals(new BigDecimal("50000"),account.getBalance());
    }
}