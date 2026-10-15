package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.onlinebank.finalization.Settlement;
import org.onlinebank.finalization.SettlementConfirmer;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SettlementConfirmerTest {

    private final SettlementConfirmer confirmer = new SettlementConfirmer();


    @Test
    void confirmsWhenNetworkIsAvailable() {
        Settlement settlement = new Settlement(
                "TR-001", new BigDecimal("200"), "USD"
        );

        confirmer.confirm(settlement, true);

        assertEquals(
                Settlement.Status.CONFIRMED,
                settlement.getStatus()
        );
    }


    @Test
    void failsWhenNetworkIsUnavailable() {
        Settlement settlement = new Settlement(
                "TR-001", new BigDecimal("200"), "USD"
        );

        confirmer.confirm(settlement, false);

        assertEquals(
                Settlement.Status.FAILED,
                settlement.getStatus()
        );
        assertEquals(
                "Settlement network unavailable",
                settlement.getReason()
        );
    }
}