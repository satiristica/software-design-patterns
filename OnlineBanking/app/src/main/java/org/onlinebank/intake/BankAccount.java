package org.onlinebank.intake;

import org.onlinebank.exceptions.InsufficientFundsException;

import java.math.BigDecimal;

public class BankAccount {

    private final String accountId;
    private final String currency;
    private BigDecimal balance;

    
    public BankAccount(String accountId, String currency, BigDecimal balance) {
        if (balance == null || balance.signum() < 0) {
            throw new IllegalArgumentException("Balance must not be negative");
        }
        this.accountId = accountId;
        this.currency = currency;
        this.balance = balance;
    }



    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds");
        }
        balance = balance.subtract(amount);
    }



    public void deposit(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.add(amount);
    }



    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }


    public String getAccountId() {
        return accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
