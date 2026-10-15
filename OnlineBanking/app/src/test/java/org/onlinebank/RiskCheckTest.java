package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.onlinebank.processing.RiskCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RiskCheckTest {

    @Test
    void newCheckIsPending() {
        RiskCheck check = new RiskCheck("TR-001");

        assertEquals("TR-001", check.getTransferId());
        assertEquals(RiskCheck.Status.PENDING, check.getStatus());
        assertEquals("", check.getReason());
    }


    @Test
    void approvesPendingCheck() {
        RiskCheck check = new RiskCheck("TR-001");

        check.approve();

        assertEquals(RiskCheck.Status.APPROVED, check.getStatus());
    }


    @Test
    void rejectsCheckWithReason() {
        RiskCheck check = new RiskCheck("TR-001");

        check.reject("High fraud risk");

        assertEquals(RiskCheck.Status.REJECTED, check.getStatus());
        assertEquals("High fraud risk", check.getReason());
    }

    
    @Test
    void revokesApprovedCheck() {
        RiskCheck check = new RiskCheck("TR-001");
        check.approve();

        check.revoke("Settlement failed");

        assertEquals(RiskCheck.Status.REVOKED, check.getStatus());
        assertEquals("Settlement failed", check.getReason());
    }

    
    @Test
    void cannotRevokePendingCheck() {
        RiskCheck check = new RiskCheck("TR-001");

        assertThrows(
                IllegalStateException.class,
                () -> check.revoke("Settlement failed")
        );

        assertEquals(RiskCheck.Status.PENDING, check.getStatus());
        assertEquals("", check.getReason());
    }
}