package org.onlinebank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onlinebank.processing.ComplianceChecker;
import org.onlinebank.processing.FraudDetector;
import org.onlinebank.processing.ProcessingRepository;
import org.onlinebank.processing.RiskCheck;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ComplianceCheckerTest {

    @TempDir
    Path directory;

    private ComplianceChecker checker;


    @BeforeEach
    void setUp() {
        ProcessingRepository repository = new ProcessingRepository(
                directory.resolve("risk-checks.json")
        );

        checker = new ComplianceChecker(
                repository,
                new FraudDetector()
        );
    }


    @Test
    void approvesAllowedTransfer() {
        RiskCheck result = checker.checkTransfer(
                "TR-001", new BigDecimal("100000"), "US"
        );

        assertEquals(RiskCheck.Status.APPROVED, result.getStatus());
        assertSavedCheck(RiskCheck.Status.APPROVED, "");
    }


    @Test
    void rejectsSuspiciousAmount() {
        checker.checkTransfer(
                "TR-001", new BigDecimal("1000001"), "US"
        );

        assertSavedCheck(
                RiskCheck.Status.REJECTED,
                "High fraud risk"
        );
    }


    @Test
    void rejectsRestrictedCountry() {
        checker.checkTransfer(
                "TR-001", new BigDecimal("100000"), "XZ"
        );

        assertSavedCheck(
                RiskCheck.Status.REJECTED,
                "Destination country is restricted"
        );
    }


    @Test
    void revokesApprovedTransfer() {
        checker.checkTransfer(
                "TR-001", new BigDecimal("100000"), "US"
        );

        checker.revokeApproval("TR-001");

        assertSavedCheck(
                RiskCheck.Status.REVOKED,
                "Settlement failed"
        );
    }

    
    @Test
    void revokingTwiceKeepsRevokedStatus() {
        checker.checkTransfer(
                "TR-001", new BigDecimal("100000"), "US"
        );

        checker.revokeApproval("TR-001");
        checker.revokeApproval("TR-001");

        assertSavedCheck(
                RiskCheck.Status.REVOKED,
                "Settlement failed"
        );
    }

    
    private void assertSavedCheck(
            RiskCheck.Status expectedStatus,
            String expectedReason
    ) {
        ProcessingRepository reopened = new ProcessingRepository(
                directory.resolve("risk-checks.json")
        );

        RiskCheck saved = reopened.findByTransferId("TR-001");

        assertNotNull(saved);
        assertEquals(expectedStatus, saved.getStatus());
        assertEquals(expectedReason, saved.getReason());
    }
}