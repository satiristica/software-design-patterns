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
import org.onlinebank.finalization.FinalizationRepository;
import org.onlinebank.finalization.Settlement;
import org.onlinebank.finalization.SettlementConfirmer;

import java.math.BigDecimal;

public class TransferFacade {

    private final AccountsData accounts;
    private final ProcessingRepository checks;
    private final FinalizationRepository settlements;
    private final TransferRepository transfers;
    private final CurrencyExchange exchange = new CurrencyExchange();
    private final SettlementConfirmer confirmer = new SettlementConfirmer();
    private final AccountLedger ledger;
    private final ComplianceChecker checker;



    public TransferFacade() {
        this(new AccountsData(), new ProcessingRepository(),
                new FinalizationRepository(), new TransferRepository());
    }



    public TransferFacade(
            AccountsData accounts, ProcessingRepository checks,
            FinalizationRepository settlements, TransferRepository transfers
    ) {
        this.accounts = accounts;
        this.checks = checks;
        this.settlements = settlements;
        this.transfers = transfers;
        ledger = new AccountLedger(accounts, exchange);
        checker = new ComplianceChecker(checks, new FraudDetector());
    }



    public TransferProcess transfer(
            String id, String accountId, BigDecimal amount,
            String targetCurrency, String country, boolean networkAvailable
    ) {
        start(id, accountId, amount, targetCurrency, country);
        return resume(id, networkAvailable);
    }



    public TransferProcess start(
            String id, String accountId, BigDecimal amount,
            String targetCurrency, String country
    ) {
        if (transfers.findByTransferId(id) != null
                || accounts.findHold(id) != null
                || checks.findByTransferId(id) != null
                || settlements.findByTransferId(id) != null) {
            throw new IllegalArgumentException("Transfer ID already exists");
        }

        BankAccount account = accounts.findAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        BigDecimal converted = exchange.convert(
                amount, account.getCurrency(), targetCurrency
        );
        transfers.save(new TransferProcess(
                id, accountId, amount, converted,
                account.getCurrency(), targetCurrency, country
        ));
        return execute(id, true, true);
    }



    public TransferProcess resume(String id, boolean networkAvailable) {
        return execute(id, networkAvailable, false);
    }



    private TransferProcess execute(
            String id, boolean networkAvailable, boolean stopAfterIntake
    ) {
        TransferProcess process = transfers.findByTransferId(id);
        if (process == null) {
            throw new IllegalArgumentException("Transfer not found");
        }
        if (process.getStatus() == TransferProcess.Status.COMPLETED
                || process.getStatus() == TransferProcess.Status.COMPENSATED) {
            return process;
        }
        if (process.getStatus() == TransferProcess.Status.COMPENSATING) {
            return compensate(id);
        }

        try {
            if (process.getStatus() == TransferProcess.Status.NEW) {
                FundsHold hold = accounts.findHold(id);
                if (hold == null) {
                    hold = ledger.placeHold(id, process.getAccountId(), process.getAmount());
                }
                if (hold.getStatus() != FundsHold.Status.HELD) {
                    throw new IllegalStateException("Expected an active hold");
                }
                process.intakeCompleted();
                transfers.save(process);
            }
            if (stopAfterIntake) {
                return process;
            }

            RiskCheck check = checks.findByTransferId(id);
            if (process.getStatus() == TransferProcess.Status.PENDING_B) {
                if (check == null) {
                    BigDecimal amountInKzt = exchange.convert(
                            process.getAmount(), process.getSourceCurrency(), "KZT"
                    );
                    check = checker.checkTransfer(
                            id, amountInKzt, process.getDestinationCountry()
                    );
                }
                if (check.getStatus() != RiskCheck.Status.APPROVED) {
                    return compensate(id);
                }
                process.processingCompleted();
                transfers.save(process);
            }
            if (check == null || check.getStatus() != RiskCheck.Status.APPROVED) {
                throw new IllegalStateException("Transfer has no approval");
            }

            Settlement settlement = settlements.findByTransferId(id);
            if (settlement == null) {
                settlement = new Settlement(
                        id, process.getConvertedAmount(), process.getTargetCurrency()
                );
                settlements.save(settlement);
            }
            if (settlement.getStatus() == Settlement.Status.PENDING) {
                confirmer.confirm(settlement, networkAvailable);
                settlements.save(settlement);
            }
            if (settlement.getStatus() == Settlement.Status.FAILED) {
                return compensate(id);
            }
            ledger.captureHold(id);
            process.complete();
            transfers.save(process);
            return process;
        } catch (RuntimeException failure) {
            try {
                compensate(id);
            } catch (RuntimeException compensationFailure) {
                failure.addSuppressed(compensationFailure);
            }
            throw failure;
        }
    }


    
    private TransferProcess compensate(String id) {
        TransferProcess process = transfers.findByTransferId(id);
        if (process == null) {
            throw new IllegalArgumentException("Transfer not found");
        }
        if (process.getStatus() == TransferProcess.Status.COMPLETED
                || process.getStatus() == TransferProcess.Status.COMPENSATED) {
            return process;
        }

        Settlement settlement = settlements.findByTransferId(id);
        FundsHold hold = accounts.findHold(id);
        if ((settlement != null && settlement.getStatus() == Settlement.Status.CONFIRMED)
                || (hold != null && hold.getStatus() == FundsHold.Status.CAPTURED)) {
            throw new IllegalStateException("Cannot compensate a settled transfer");
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
        return process;
    }
}
