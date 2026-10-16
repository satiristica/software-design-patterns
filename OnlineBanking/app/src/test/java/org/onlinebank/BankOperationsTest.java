package org.onlinebank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onlinebank.facade.BankOperations;
import org.onlinebank.facade.TransferFacade;
import org.onlinebank.facade.TransferProcess;
import org.onlinebank.facade.TransferRepository;
import org.onlinebank.finalization.AuditLogger;
import org.onlinebank.finalization.FinalizationRepository;
import org.onlinebank.finalization.NotificationService;
import org.onlinebank.intake.AccountsData;
import org.onlinebank.intake.BankAccount;
import org.onlinebank.processing.ProcessingRepository;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BankOperationsTest {

    @TempDir
    Path directory;

    private AccountsData accounts;
    private ProcessingRepository checks;
    private FinalizationRepository settlements;
    private TransferRepository transfers;
    private BankOperations admin;

    @BeforeEach
    void setUp() {
        accounts = new AccountsData(
                directory.resolve("accounts.json")
        );

        checks = new ProcessingRepository(
                directory.resolve("checks.json")
        );

        settlements = new FinalizationRepository(
                directory.resolve("settlements.json")
        );

        transfers = new TransferRepository(
                directory.resolve("transfers.json")
        );

        admin = new BankOperations(
                accounts, checks, settlements, transfers
        );

        accounts.saveAccount(new BankAccount(
                "ACC-001", "John", "DEMO-IBAN", "KZT",
                new BigDecimal("100000")
        ));
    }

    @Test
    void readsSavedBalance() {
        assertEquals(
                new BigDecimal("100000"),
                admin.getBalance("ACC-001")
        );
    }

    @Test
    void rejectsMissingAccountAndTransfer() {
        assertThrows(
                IllegalArgumentException.class,
                () -> admin.getBalance("missing")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> admin.inspectTransfer("missing")
        );
    }

    @Test
    void inspectsTransferBeforeSubsystemsStart() {
        transfers.save(new TransferProcess(
                "TR-001", "ACC-001",
                new BigDecimal("90000"),
                new BigDecimal("200.00"),
                "KZT", "USD", "US"
        ));

        String report = admin.inspectTransfer("TR-001");

        assertTrue(report.contains("Status: NEW"));
        assertTrue(report.contains("Hold: NOT_STARTED"));
        assertTrue(report.contains("Risk check: NOT_STARTED"));
        assertTrue(report.contains("Settlement: NOT_STARTED"));
    }

    @Test
    void inspectsCompletedTransferWithoutChangingBalance() {
        TransferFacade facade = new TransferFacade(
                accounts,
                checks,
                settlements,
                transfers,
                new AuditLogger(directory.resolve("audit.json")),
                new NotificationService(message -> {})
        );

        facade.transfer(
                "TR-001", "ACC-001",
                new BigDecimal("90000"),
                "USD", "US", true
        );

        assertEquals("""
                Transfer: TR-001
                Account: ACC-001
                Amount: 90000 KZT
                Converted amount: 200.00 USD
                Status: COMPLETED
                Hold: CAPTURED
                Risk check: APPROVED
                Settlement: CONFIRMED""",
                admin.inspectTransfer("TR-001")
        );

        assertEquals(
                new BigDecimal("10000"),
                admin.getBalance("ACC-001")
        );
    }
}