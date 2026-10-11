package org.onlinebank.intake;

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
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.iban = iban;
        this.currency = currency;
        this.balance = balance;
        this.status = Status.ACTIVE;
    }


    public void withdraw(BigDecimal amount) {
        if (status != Status.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }


        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Not enough funds");
        }

        balance = balance.subtract(amount);
    }


    public void deposit(BigDecimal amount) {
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