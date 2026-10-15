package org.onlinebank.facade;

import org.onlinebank.intake.AccountLedger;
import org.onlinebank.intake.AccountsData;
import org.onlinebank.intake.BankAccount;
import org.onlinebank.intake.CurrencyExchange;
import org.onlinebank.intake.FundsHold;
import org.onlinebank.processing.ComplianceChecker;
import org.onlinebank.processing.FraudDetector;
import org.onlinebank.processing.ProcessingRepository;
import org.onlinebank.processing.RiskCheck;
import org.onlinebank.finalization.AuditLogger;
import org.onlinebank.finalization.FinalizationRepository;
import org.onlinebank.finalization.NotificationService;
import org.onlinebank.finalization.Settlement;
import org.onlinebank.finalization.SettlementConfirmer;

import java.math.BigDecimal;

public class TransferFacade {

    private final AccountsData accounts;
    private final ProcessingRepository checks;
    private final FinalizationRepository settlements;
    private final TransferRepository transfers;
    private final AuditLogger audit;
    private final NotificationService notifications;

    private final CurrencyExchange exchange = new CurrencyExchange();
    private final SettlementConfirmer confirmer = new SettlementConfirmer();
    private final AccountLedger ledger;
    private final ComplianceChecker checker;

    public TransferFacade() {
        this(
                new AccountsData(),
                new ProcessingRepository(),
                new FinalizationRepository(),
                new TransferRepository(),
                new AuditLogger(),
                new NotificationService()
        );
    }

    public TransferFacade(
            AccountsData accounts,
            ProcessingRepository checks,
            FinalizationRepository settlements,
            TransferRepository transfers,
            AuditLogger audit,
            NotificationService notifications
    ) {
        this.accounts = accounts;
        this.checks = checks;
        this.settlements = settlements;
        this.transfers = transfers;
        this.audit = audit;
        this.notifications = notifications;

        this.ledger = new AccountLedger(accounts, exchange);
        this.checker = new ComplianceChecker(checks, new FraudDetector());
    }

    public TransferProcess transfer(
            String transferId,
            String accountId,
            BigDecimal amount,
            String targetCurrency,
            String destinationCountry,
            boolean networkAvailable
    ) {
        if (transfers.findByTransferId(transferId) != null
                || accounts.findHold(transferId) != null
                || checks.findByTransferId(transferId) != null
                || settlements.findByTransferId(transferId) != null) {
            throw new IllegalArgumentException("Transfer ID already exists");
        }

        BankAccount account = accounts.findAccount(accountId);

        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        BigDecimal convertedAmount = exchange.convert(
                amount,
                account.getCurrency(),
                targetCurrency
        );

        TransferProcess process = new TransferProcess(
                transferId,
                accountId,
                amount,
                convertedAmount,
                account.getCurrency(),
                targetCurrency,
                destinationCountry
        );

        transfers.save(process);

        return resume(transferId, networkAvailable);
    }

    public TransferProcess resume(
            String transferId,
            boolean networkAvailable
    ) {
        TransferProcess process = requireProcess(transferId);

        if (isFinished(process)) {
            return process;
        }

        try {
            return advance(process, networkAvailable);
        } catch (RuntimeException failure) {
            try {

                Settlement saved = settlements.findByTransferId(transferId);

                if (saved == null
                        || saved.getStatus() != Settlement.Status.CONFIRMED) {
                    TransferProcess persisted = requireProcess(transferId);

                    if (!isFinished(persisted)) {
                        compensate(persisted);
                    }
                }
            } catch (RuntimeException compensationFailure) {
                failure.addSuppressed(compensationFailure);
            }

            throw failure;
        }
    }

    private TransferProcess advance(
            TransferProcess process,
            boolean networkAvailable
    ) {
        String id = process.getTransferId();

        if (process.getStatus() == TransferProcess.Status.COMPENSATING) {
            return compensate(process);
        }


        if (process.getStatus() == TransferProcess.Status.NEW) {
            FundsHold hold = accounts.findHold(id);

            if (hold == null) {
                hold = ledger.placeHold(
                        id,
                        process.getAccountId(),
                        process.getAmount()
                );
            }

            if (hold.getStatus() != FundsHold.Status.HELD) {
                throw new IllegalStateException("Expected an active hold");
            }

            process.intakeCompleted();
            transfers.save(process);
        }


        if (process.getStatus() == TransferProcess.Status.PENDING_B) {
            RiskCheck check = checks.findByTransferId(id);

            if (check == null) {
                BigDecimal amountInKzt = exchange.convert(
                        process.getAmount(),
                        process.getSourceCurrency(),
                        "KZT"
                );

                check = checker.checkTransfer(
                        id,
                        amountInKzt,
                        process.getDestinationCountry()
                );
            }

            if (check.getStatus() != RiskCheck.Status.APPROVED) {
                return compensate(process);
            }

            process.processingCompleted();
            transfers.save(process);
        }


        RiskCheck check = checks.findByTransferId(id);

        if (check == null || check.getStatus() != RiskCheck.Status.APPROVED) {
            throw new IllegalStateException("Transfer has no approval");
        }

        Settlement settlement = settlements.findByTransferId(id);

        if (settlement == null) {
            settlement = new Settlement(
                    id,
                    process.getConvertedAmount(),
                    process.getTargetCurrency()
            );

            settlements.save(settlement);
        }

        if (settlement.getStatus() == Settlement.Status.PENDING) {
            confirmer.confirm(settlement, networkAvailable);
            settlements.save(settlement);
        }

        if (settlement.getStatus() == Settlement.Status.FAILED) {
            return compensate(process);
        }

        ledger.captureHold(id);

        process.complete();
        transfers.save(process);

        report(process);
        return process;
    }

    private TransferProcess compensate(TransferProcess process) {
        String id = process.getTransferId();

        Settlement settlement = settlements.findByTransferId(id);
        FundsHold hold = accounts.findHold(id);

        if ((settlement != null
                && settlement.getStatus() == Settlement.Status.CONFIRMED)
                || (hold != null
                && hold.getStatus() == FundsHold.Status.CAPTURED)) {
            throw new IllegalStateException(
                    "Cannot compensate a settled transfer"
            );
        }

        if (process.getStatus() != TransferProcess.Status.COMPENSATING) {
            process.startCompensation();
            transfers.save(process);
        }

        
        RiskCheck check = checks.findByTransferId(id);

        if (check != null && check.getStatus() == RiskCheck.Status.APPROVED) {
            checker.revokeApproval(id);
        }

        
        if (hold != null) {
            ledger.releaseHold(id);
        }

        process.finishCompensation();
        transfers.save(process);

        report(process);
        return process;
    }

    private TransferProcess requireProcess(String transferId) {
        TransferProcess process = transfers.findByTransferId(transferId);

        if (process == null) {
            throw new IllegalArgumentException("Transfer not found");
        }

        return process;
    }

    private boolean isFinished(TransferProcess process) {
        return process.getStatus() == TransferProcess.Status.COMPLETED
                || process.getStatus() == TransferProcess.Status.COMPENSATED;
    }

    private void report(TransferProcess process) {
        String id = process.getTransferId();
        String message = process.getStatus().name();

        try {
            audit.log(id, message);
        } catch (RuntimeException e) {
            System.err.println(
                    "Audit failed for " + id + ": " + e.getMessage()
            );
        }

        try {
            notifications.send(id, message);
        } catch (RuntimeException e) {
            System.err.println(
                    "Notification failed for " + id + ": " + e.getMessage()
            );
        }
    }
}