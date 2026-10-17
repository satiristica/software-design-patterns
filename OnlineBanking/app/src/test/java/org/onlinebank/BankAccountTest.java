package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.onlinebank.exceptions.InsufficientFundsException;
import org.onlinebank.intake.BankAccount;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankAccountTest {

    @Test
    void withdrawReducesBalance() {
        BankAccount account = new BankAccount(
                "ACC-001",
                "KZT",
                new BigDecimal("100000")
        );

        account.withdraw(new BigDecimal("50000"));

        assertEquals(new BigDecimal("50000"),account.getBalance());
    }

    @Test
    void insufficientFundsDoNotChangeBalance() {
        BankAccount account = new BankAccount(
                "ACC-001", "KZT",
                new BigDecimal("100000")
        );

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(new BigDecimal("150000"))
        );

        assertEquals(new BigDecimal("100000"), account.getBalance());
    }

    @Test
    void rejectsInvalidWithdrawalAmounts() {
        BankAccount account = accountWithBalance(new BigDecimal("100000"));

        assertThrows(IllegalArgumentException.class, () -> account.withdraw(null));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(new BigDecimal("-10")));

        assertEquals(new BigDecimal("100000"), account.getBalance());
    }

    @Test
    void rejectsInvalidDepositAmounts() {
        BankAccount account = accountWithBalance(new BigDecimal("100000"));

        assertThrows(IllegalArgumentException.class, () -> account.deposit(null));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(new BigDecimal("-10")));

        assertEquals(new BigDecimal("100000"), account.getBalance());
    }

    @Test
    void startingBalanceMustBeNonNegative() {
        assertThrows(IllegalArgumentException.class, () -> accountWithBalance(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> accountWithBalance(new BigDecimal("-100"))
        );

        assertEquals(BigDecimal.ZERO, accountWithBalance(BigDecimal.ZERO).getBalance());
    }

    private BankAccount accountWithBalance(BigDecimal balance) {
        return new BankAccount(
                "ACC-001", "KZT", balance
        );
    }
}
