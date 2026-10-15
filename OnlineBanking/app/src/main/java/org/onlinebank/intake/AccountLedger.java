package org.onlinebank.intake;

import java.math.BigDecimal;

public class AccountLedger {

    private final AccountsData accountsData;
    private final CurrencyExchange currencyExchange;

    
    public AccountLedger(
            AccountsData accountsData,
            CurrencyExchange currencyExchange
    ) {
        this.accountsData = accountsData;
        this.currencyExchange = currencyExchange;
    }


    public BigDecimal convert(
            String accountId,
            BigDecimal amount,
            String targetCurrency
    ) {
        BankAccount account = requireAccount(accountId);

        return currencyExchange.convert(
                amount,
                account.getCurrency(),
                targetCurrency
        );
    }


    public FundsHold placeHold(
            String transferId,
            String accountId,
            BigDecimal amount
    ) {
        if (transferId == null || transferId.isBlank()) {
            throw new IllegalArgumentException("Transfer ID is required");
        }

        if (accountsData.findHold(transferId) != null) {
            throw new IllegalStateException("Transfer already has a hold");
        }

        BankAccount account = requireAccount(accountId);
        FundsHold hold = new FundsHold(transferId, accountId, amount);

        account.withdraw(amount);
        hold.place();

        accountsData.save(account, hold);

        return hold;
    }


    public void captureHold(String transferId) {
        FundsHold hold = requireHold(transferId);

        if (hold.getStatus() == FundsHold.Status.CAPTURED) {
            return;
        }

        BankAccount account = requireAccount(hold.getAccountId());

        hold.capture();
        accountsData.save(account, hold);
    }


    public void releaseHold(String transferId) {
        FundsHold hold = requireHold(transferId);

        if (hold.getStatus() == FundsHold.Status.RELEASED) {
            return;
        }

        BankAccount account = requireAccount(hold.getAccountId());

        hold.release();
        account.deposit(hold.getAmount());

        accountsData.save(account, hold);
    }


    private BankAccount requireAccount(String accountId) {
        BankAccount account = accountsData.findAccount(accountId);

        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        return account;
    }


    private FundsHold requireHold(String transferId) {
        FundsHold hold = accountsData.findHold(transferId);

        if (hold == null) {
            throw new IllegalArgumentException("Hold not found");
        }

        return hold;
    }
}