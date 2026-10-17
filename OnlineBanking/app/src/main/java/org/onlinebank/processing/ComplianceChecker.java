package org.onlinebank.processing;

import java.math.BigDecimal;

public class ComplianceChecker {

    private final ProcessingRepository repository;
    private final FraudDetector fraudDetector;


    public ComplianceChecker(
            ProcessingRepository repository,
            FraudDetector fraudDetector
    ) {
        this.repository = repository;
        this.fraudDetector = fraudDetector;
    }


    
    public RiskCheck checkTransfer(
            String transferId,
            BigDecimal amountInKzt,
            String destinationCountry
    ) {
        RiskCheck check = new RiskCheck(transferId);

        if (destinationCountry == null
                || !destinationCountry.trim().matches("[A-Za-z]{2}")) {
            throw new IllegalArgumentException(
                    "Destination country must be a two-letter code"
            );
        }

        if (repository.findByTransferId(transferId) != null) {
            throw new IllegalStateException(
                    "Transfer already has a risk check"
            );
        }

        boolean suspicious = fraudDetector.isSuspicious(amountInKzt);

    
        if ("XZ".equalsIgnoreCase(destinationCountry.trim())) {
            check.reject("Destination country is restricted");
        } else if (suspicious) {
            check.reject("High fraud risk");
        } else {
            check.approve();
        }

        repository.save(check);

        return check;
    }

    

    public void revokeApproval(String transferId) {
        RiskCheck check = repository.findByTransferId(transferId);

        if (check == null) {
            throw new IllegalArgumentException("Risk check not found");
        }

        if (check.getStatus() == RiskCheck.Status.REVOKED) {
            return;
        }

        check.revoke("Settlement failed");
        repository.save(check);
    }
}