package org.onlinebank.intake;

import java.math.BigDecimal;

public class FundsHold {

    public enum Status {
        NEW,
        HELD,
        CAPTURED,
        RELEASED
    }

    
    private final String transferId;
    private final String accountId;
    private final BigDecimal amount;

    private Status status;


    public FundsHold(String transferId, String accountId, BigDecimal amount) {
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.transferId = transferId;
        this.accountId = accountId;
        this.amount = amount;
        this.status = Status.NEW;
    }


    public void place() {
        if (status != Status.NEW) {
            throw new IllegalStateException("Only new holds can be placed");
        }

        status = Status.HELD;
    }


    public void capture() {
        if (status != Status.HELD) {
            throw new IllegalStateException("Only held funds can be captured");
        }

        status = Status.CAPTURED;
    }


    public void release() {
        if (status != Status.HELD) {
            throw new IllegalStateException("Only held funds can be released");
        }

        status = Status.RELEASED;
    }


    public String getTransferId() {
        return transferId;
    }


    public String getAccountId() {
        return accountId;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public Status getStatus() {
        return status;
    }
}