package org.onlinebank.processing;

public class RiskCheck {

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        REVOKED
    }


    private final String transferId;
    private Status status;
    private String reason;


    public RiskCheck(String transferId) {
        if (transferId == null || transferId.isBlank()) {
            throw new IllegalArgumentException("Transfer ID is required");
        }

        this.transferId = transferId;
        this.status = Status.PENDING;
        this.reason = "";
    }


    public void approve() {
        if (status != Status.PENDING) {
            throw new IllegalStateException(
                    "Only pending checks can be approved"
            );
        }

        status = Status.APPROVED;
    }

    

    public void reject(String reason) {
        if (status != Status.PENDING) {
            throw new IllegalStateException("Only pending checks can be rejected");
        }

        this.reason = reason;
        status = Status.REJECTED;
    }



    public void revoke(String reason) {
        if (status != Status.APPROVED) {
            throw new IllegalStateException("Only approved checks can be revoked");
        }

        this.reason = reason;
        status = Status.REVOKED;
    }


    
    public String getTransferId() {
        return transferId;
    }


    public Status getStatus() {
        return status;
    }


    public String getReason() {
        return reason;
    }
}