package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onlinebank.finalization.FinalizationRepository;
import org.onlinebank.finalization.Settlement;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FinalizationRepositoryTest {

    @TempDir
    Path directory;

    
    @Test
    void restoresConfirmedSettlementAfterReopening() {
        Path file = directory.resolve("settlements.json");
        FinalizationRepository repository =
                new FinalizationRepository(file);

        Settlement settlement = new Settlement(
                "TR-001", new BigDecimal("200"), "USD"
        );
        settlement.confirm();
        repository.save(settlement);

        FinalizationRepository reopened =
                new FinalizationRepository(file);
        Settlement restored = reopened.findByTransferId("TR-001");

        assertNotNull(restored);
        assertEquals("TR-001", restored.getTransferId());
        assertEquals(new BigDecimal("200"), restored.getAmount());
        assertEquals("USD", restored.getCurrency());
        assertEquals(Settlement.Status.CONFIRMED, restored.getStatus());
    }


    @Test
    void savesUpdatedStatusAndFailureReason() {
        Path file = directory.resolve("settlements.json");
        FinalizationRepository repository =
                new FinalizationRepository(file);

        Settlement settlement = new Settlement(
                "TR-001", new BigDecimal("200"), "USD"
        );
        repository.save(settlement);

        settlement.fail("Settlement network unavailable");
        repository.save(settlement);

        FinalizationRepository reopened =
                new FinalizationRepository(file);
        Settlement restored = reopened.findByTransferId("TR-001");

        assertNotNull(restored);
        assertEquals(Settlement.Status.FAILED, restored.getStatus());
        assertEquals(
                "Settlement network unavailable",
                restored.getReason()
        );
    }
}