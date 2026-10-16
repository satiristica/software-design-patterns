package org.onlinebank.intake;

import org.onlinebank.exceptions.InsufficientFundsException;

import java.math.BigDecimal;

public class BankAccount {

    public enum Status {
        ACTIVE,
        FROZEN,
        CLOSED
    }

    private final String accountId;
    private final String ownerName;
    private final String iban;
    private final String currency;

    private BigDecimal balance;
    private Status status;

    public BankAccount(
            String accountId,
            String ownerName,
            String iban,
            String currency,
            BigDecimal balance
    ) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance must not be negative");
        }

        this.accountId = accountId;
        this.ownerName = ownerName;
        this.iban = iban;
        this.currency = currency;
        this.balance = balance;
        this.status = Status.ACTIVE;
    }


    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (status != Status.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }


        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds");
        }

        balance = balance.subtract(amount);
    }


    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        balance = balance.add(amount);
    }


    public void freeze() {
        if (status != Status.ACTIVE) {
            throw new IllegalStateException("Only active accounts can be frozen");
        }

        status = Status.FROZEN;
    }


    public void activate() {
        if (status != Status.FROZEN) {
            throw new IllegalStateException("Only frozen accounts can be activated");
        }

        status = Status.ACTIVE;
    }


    public void close() {
        status = Status.CLOSED;
    }


    public String getAccountId() {
        return accountId;
    }


    public String getOwnerName() {
        return ownerName;
    }


    public String getIban() {
        return iban;
    }


    public String getCurrency() {
        return currency;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    
    public Status getStatus() {
        return status;
    }
}
