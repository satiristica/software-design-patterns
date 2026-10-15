package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.onlinebank.processing.FraudDetector;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FraudDetectorTest {

    private final FraudDetector detector = new FraudDetector();

    @Test
    void amountBelowThresholdIsNotSuspicious() {
        assertFalse(
                detector.isSuspicious(new BigDecimal("100000"))
        );
    }

    
    @Test
    void amountAtThresholdIsNotSuspicious() {
        assertFalse(
                detector.isSuspicious(new BigDecimal("1000000"))
        );
    }


    @Test
    void amountAboveThresholdIsSuspicious() {
        assertTrue(
                detector.isSuspicious(new BigDecimal("1000001"))
        );
    }


    @Test
    void rejectsZeroAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> detector.isSuspicious(BigDecimal.ZERO)
        );
    }


    @Test
    void rejectsNegativeAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> detector.isSuspicious(new BigDecimal("-100"))
        );
    }
}