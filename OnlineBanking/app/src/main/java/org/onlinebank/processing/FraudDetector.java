package org.onlinebank.processing;

import java.math.BigDecimal;

public class FraudDetector {

    private static final BigDecimal HIGH_RISK_THRESHOLD =
            new BigDecimal("1000000");

            
    public boolean isSuspicious(BigDecimal amountInKzt) {
        if (amountInKzt == null
                || amountInKzt.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        return amountInKzt.compareTo(HIGH_RISK_THRESHOLD) > 0;
    }
}