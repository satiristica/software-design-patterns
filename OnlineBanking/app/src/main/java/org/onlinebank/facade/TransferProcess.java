package org.onlinebank.facade;

import java.math.BigDecimal;

public class TransferProcess {

    public enum Status {
        NEW,
        PENDING_B,
        PENDING_C,
        COMPENSATING,
        COMPLETED,
        COMPENSATED
    }

    private final String transferId;
    private final String accountId;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;
    private final String sourceCurrency;
    private final String targetCurrency;
    private final String destinationCountry;

    private Status status;

    public TransferProcess(
            String transferId,
            String accountId,
            BigDecimal amount,
            BigDecimal convertedAmount,
            String sourceCurrency,
            String targetCurrency,
            String destinationCountry
    ) {
        requireText(transferId);
        requireText(accountId);
        requireText(sourceCurrency);
        requireText(targetCurrency);
        requireText(destinationCountry);

        if (amount == null || amount.signum() <= 0
                || convertedAmount == null
                || convertedAmount.signum() <= 0) {
            throw new IllegalArgumentException("Amounts must be positive");
        }

        this.transferId = transferId;
        this.accountId = accountId;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.destinationCountry = destinationCountry;
        this.status = Status.NEW;
    }


    
    public void intakeCompleted() {
        changeStatus(Status.NEW, Status.PENDING_B);
    }



    public void processingCompleted() {
        changeStatus(Status.PENDING_B, Status.PENDING_C);
    }



    public void complete() {
        changeStatus(Status.PENDING_C, Status.COMPLETED);
    }



    public void startCompensation() {
        if (status != Status.NEW
                && status != Status.PENDING_B
                && status != Status.PENDING_C) {
            throw new IllegalStateException(
                    "Cannot start compensation from " + status
            );
        }

        status = Status.COMPENSATING;
    }



    public void finishCompensation() {
        changeStatus(Status.COMPENSATING, Status.COMPENSATED);
    }



    private void changeStatus(Status expected, Status next) {
        if (status != expected) {
            throw new IllegalStateException(
                    "Expected " + expected + ", but was " + status
            );
        }

        status = next;
    }



    private static void requireText(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Transfer details must not be empty"
            );
        }
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

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public Status getStatus() {
        return status;
    }
}