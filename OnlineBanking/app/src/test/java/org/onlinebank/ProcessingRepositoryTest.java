package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onlinebank.processing.ProcessingRepository;
import org.onlinebank.processing.RiskCheck;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessingRepositoryTest {

    @TempDir
    Path directory;

    
    @Test
    void restoresApprovedCheckAfterReopening() {
        Path file = directory.resolve("risk-checks.json");
        ProcessingRepository repository = new ProcessingRepository(file);

        RiskCheck check = new RiskCheck("TR-001");
        check.approve();
        repository.save(check);

        ProcessingRepository reopened = new ProcessingRepository(file);
        RiskCheck restored = reopened.findByTransferId("TR-001");

        assertEquals("TR-001", restored.getTransferId());
        assertEquals(RiskCheck.Status.APPROVED, restored.getStatus());
    }


    @Test
    void restoresRejectedCheckWithReason() {
        Path file = directory.resolve("risk-checks.json");
        ProcessingRepository repository = new ProcessingRepository(file);

        RiskCheck check = new RiskCheck("TR-001");
        check.reject("High fraud risk");
        repository.save(check);

        ProcessingRepository reopened = new ProcessingRepository(file);
        RiskCheck restored = reopened.findByTransferId("TR-001");

        assertEquals(RiskCheck.Status.REJECTED, restored.getStatus());
        assertEquals("High fraud risk", restored.getReason());
    }

    
    @Test
    void savesUpdatedStatus() {
        Path file = directory.resolve("risk-checks.json");
        ProcessingRepository repository = new ProcessingRepository(file);

        RiskCheck check = new RiskCheck("TR-001");
        check.approve();
        repository.save(check);

        check.revoke("Settlement failed");
        repository.save(check);

        ProcessingRepository reopened = new ProcessingRepository(file);
        RiskCheck restored = reopened.findByTransferId("TR-001");

        assertEquals(RiskCheck.Status.REVOKED, restored.getStatus());
        assertEquals("Settlement failed", restored.getReason());
    }
}