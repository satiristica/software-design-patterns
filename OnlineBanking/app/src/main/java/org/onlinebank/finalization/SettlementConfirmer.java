package org.onlinebank.finalization;

public class SettlementConfirmer {

    public void confirm(Settlement settlement, boolean networkAvailable) {
        if (settlement == null) {
            throw new IllegalArgumentException("Settlement is required");
        }

        
        if (networkAvailable) {
            settlement.confirm();
        } else {
            settlement.fail("Settlement network unavailable");
        }
    }
}