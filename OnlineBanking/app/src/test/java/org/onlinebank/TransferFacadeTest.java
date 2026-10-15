package org.onlinebank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.onlinebank.facade.TransferFacade;
import org.onlinebank.facade.TransferProcess;
import org.onlinebank.facade.TransferRepository;
import org.onlinebank.finalization.AuditLogger;
import org.onlinebank.finalization.FinalizationRepository;
import org.onlinebank.finalization.NotificationService;
import org.onlinebank.finalization.Settlement;
import org.onlinebank.intake.AccountLedger;
import org.onlinebank.intake.AccountsData;
import org.onlinebank.intake.BankAccount;
import org.onlinebank.intake.CurrencyExchange;
import org.onlinebank.intake.FundsHold;
import org.onlinebank.processing.ComplianceChecker;
import org.onlinebank.processing.FraudDetector;
import org.onlinebank.processing.ProcessingRepository;
import org.onlinebank.processing.RiskCheck;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransferFacadeTest {

    @TempDir
    Path directory;

    private AccountsData accounts;
    private ProcessingRepository checks;
    private FinalizationRepository settlements;
    private TransferRepository transfers;
    private TransferFacade facade;

    @BeforeEach
    void setUp() {
        reopenStorage();

        accounts.saveAccount(new BankAccount(
                "ACC-001", "John", "DEMO-IBAN", "KZT",
                new BigDecimal("100000")
        ));

        facade = createFacade();
    }

    @Test
    void successfulTransferCapturesMoney() {
        facade.transfer(
                "TR-001", "ACC-001", new BigDecimal("90000"),
                "USD", "US", true
        );

        assertSavedState(
                "10000", TransferProcess.Status.COMPLETED,
                FundsHold.Status.CAPTURED, RiskCheck.Status.APPROVED
        );

        Settlement saved = settlements.findByTransferId("TR-001");

        assertEquals(Settlement.Status.CONFIRMED, saved.getStatus());
        assertEquals(0, new BigDecimal("200").compareTo(saved.getAmount()));
        assertEquals("USD", saved.getCurrency());
    }

    @Test
    void rejectedCheckReturnsReservedMoney() {
        facade.transfer(
                "TR-001", "ACC-001", new BigDecimal("90000"),
                "USD", "XZ", true
        );

        assertSavedState(
                "100000", TransferProcess.Status.COMPENSATED,
                FundsHold.Status.RELEASED, RiskCheck.Status.REJECTED
        );

        assertNull(settlements.findByTransferId("TR-001"));
    }

    @Test
    void failedSettlementRevokesApprovalAndReturnsMoney() {
        facade.transfer(
                "TR-001", "ACC-001", new BigDecimal("90000"),
                "USD", "US", false
        );

        assertSavedState(
                "100000", TransferProcess.Status.COMPENSATED,
                FundsHold.Status.RELEASED, RiskCheck.Status.REVOKED
        );

        assertEquals(
                Settlement.Status.FAILED,
                settlements.findByTransferId("TR-001").getStatus()
        );
    }

    @Test
    void restartedFacadeContinuesSavedTransferWithoutWithdrawingTwice() {
        TransferProcess process = new TransferProcess(
                "TR-001", "ACC-001",
                new BigDecimal("90000"), new BigDecimal("200"),
                "KZT", "USD", "US"
        );

        transfers.save(process);

        AccountLedger ledger = new AccountLedger(
                accounts, new CurrencyExchange()
        );

        ledger.placeHold("TR-001", "ACC-001", process.getAmount());
        process.intakeCompleted();
        transfers.save(process);

        ComplianceChecker checker = new ComplianceChecker(
                checks, new FraudDetector()
        );

        checker.checkTransfer("TR-001", process.getAmount(), "US");
        process.processingCompleted();
        transfers.save(process);

        assertSavedState(
                "10000", TransferProcess.Status.PENDING_C,
                FundsHold.Status.HELD, RiskCheck.Status.APPROVED
        );

        assertNull(settlements.findByTransferId("TR-001"));

        // Имитируем перезапуск: новые хранилища и новый фасад.
        reopenStorage();
        facade = createFacade();

        facade.resume("TR-001", true);

        assertSavedState(
                "10000", TransferProcess.Status.COMPLETED,
                FundsHold.Status.CAPTURED, RiskCheck.Status.APPROVED
        );

        assertEquals(
                Settlement.Status.CONFIRMED,
                settlements.findByTransferId("TR-001").getStatus()
        );

        // Повторный вызов не должен снова списать деньги.
        facade.resume("TR-001", true);

        assertSavedState(
                "10000", TransferProcess.Status.COMPLETED,
                FundsHold.Status.CAPTURED, RiskCheck.Status.APPROVED
        );
    }

    private void reopenStorage() {
        accounts = new AccountsData(
                directory.resolve("accounts.json")
        );

        checks = new ProcessingRepository(
                directory.resolve("risk-checks.json")
        );

        settlements = new FinalizationRepository(
                directory.resolve("settlements.json")
        );

        transfers = new TransferRepository(
                directory.resolve("transfers.json")
        );
    }

    private TransferFacade createFacade() {
        return new TransferFacade(
                accounts,
                checks,
                settlements,
                transfers,
                new AuditLogger(directory.resolve("audit.json")),
                new NotificationService()
        );
    }

    private void assertSavedState(
            String balance,
            TransferProcess.Status transferStatus,
            FundsHold.Status holdStatus,
            RiskCheck.Status checkStatus
    ) {
        assertEquals(
                0,
                new BigDecimal(balance).compareTo(
                        accounts.findAccount("ACC-001").getBalance()
                )
        );

        assertEquals(
                transferStatus,
                transfers.findByTransferId("TR-001").getStatus()
        );

        assertEquals(
                holdStatus,
                accounts.findHold("TR-001").getStatus()
        );

        assertEquals(
                checkStatus,
                checks.findByTransferId("TR-001").getStatus()
        );
    }
}