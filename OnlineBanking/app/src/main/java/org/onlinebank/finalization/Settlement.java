package org.onlinebank.finalization;

import java.math.BigDecimal;

public class Settlement {

    public enum Status {
        PENDING,
        CONFIRMED,
        FAILED
    }


    private final String transferId;
    private final BigDecimal amount;
    private final String currency;

    private Status status;
    private String reason;


    public Settlement(String transferId, BigDecimal amount, String currency) {
        if (transferId == null || transferId.isBlank()) {
            throw new IllegalArgumentException("Transfer ID is required");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }

        this.transferId = transferId;
        this.amount = amount;
        this.currency = currency;
        this.status = Status.PENDING;
        this.reason = "";
    }


    public void confirm() {
        if (status != Status.PENDING) {
            throw new IllegalStateException(
                    "Only pending settlements can be confirmed"
            );
        }

        status = Status.CONFIRMED;
    }


    public void fail(String reason) {
        if (status != Status.PENDING) {
            throw new IllegalStateException(
                    "Only pending settlements can fail"
            );
        }

        this.reason = reason;
        status = Status.FAILED;
    }


    public String getTransferId() {
        return transferId;
    }

    public BigDecimal getAmount() {
        return amount;
    }


    public String getCurrency() {
        return currency;
    }


    public Status getStatus() {
        return status;
    }


    public String getReason() {
        return reason;
    }
}