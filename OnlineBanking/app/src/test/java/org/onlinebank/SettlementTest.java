package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.onlinebank.finalization.Settlement;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SettlementTest {

    @Test
    void confirmsPendingSettlement() {
        Settlement settlement = new Settlement(
                "TR-001", new BigDecimal("200"), "USD"
        );

        settlement.confirm();

        assertEquals(Settlement.Status.CONFIRMED, settlement.getStatus());
    }


    @Test
    void failsSettlementWithReason() {
        Settlement settlement = new Settlement(
                "TR-001", new BigDecimal("200"), "USD"
        );

        settlement.fail("Settlement network unavailable");

        assertEquals(Settlement.Status.FAILED, settlement.getStatus());
        assertEquals(
                "Settlement network unavailable",
                settlement.getReason()
        );
    }

    
    @Test
    void cannotConfirmFailedSettlement() {
        Settlement settlement = new Settlement(
                "TR-001", new BigDecimal("200"), "USD"
        );
        settlement.fail("Settlement network unavailable");

        assertThrows(
                IllegalStateException.class,
                settlement::confirm
        );

        assertEquals(Settlement.Status.FAILED, settlement.getStatus());
    }
}