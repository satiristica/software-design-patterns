package org.onlinebank.facade;

import org.onlinebank.intake.AccountsData;
import org.onlinebank.intake.BankAccount;
import org.onlinebank.intake.FundsHold;
import org.onlinebank.processing.ProcessingRepository;
import org.onlinebank.processing.RiskCheck;
import org.onlinebank.finalization.FinalizationRepository;
import org.onlinebank.finalization.Settlement;

import java.math.BigDecimal;

public class BankOperations {

    private final AccountsData accounts;
    private final ProcessingRepository checks;
    private final FinalizationRepository settlements;
    private final TransferRepository transfers;

    public BankOperations() {
        this(
                new AccountsData(),
                new ProcessingRepository(),
                new FinalizationRepository(),
                new TransferRepository()
        );
    }



    public BankOperations(
            AccountsData accounts,
            ProcessingRepository checks,
            FinalizationRepository settlements,
            TransferRepository transfers
    ) {
        this.accounts = accounts;
        this.checks = checks;
        this.settlements = settlements;
        this.transfers = transfers;
    }



    public BigDecimal getBalance(String accountId) {
        BankAccount account = accounts.findAccount(accountId);

        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        return account.getBalance();
    }



    public String inspectTransfer(String transferId) {
        TransferProcess process = transfers.findByTransferId(transferId);

        if (process == null) {
            throw new IllegalArgumentException("Transfer not found");
        }

        FundsHold hold = accounts.findHold(transferId);
        RiskCheck check = checks.findByTransferId(transferId);
        Settlement settlement = settlements.findByTransferId(transferId);

        String holdStatus;

        if (hold == null) {
            holdStatus = "NOT_STARTED";
        } else {
            holdStatus = hold.getStatus().name();
        }

        String checkStatus;

        if (check == null) {
            checkStatus = "NOT_STARTED";
        } else {
            checkStatus = check.getStatus().name();
        }

        String settlementStatus;

        if (settlement == null) {
            settlementStatus = "NOT_STARTED";
        } else {
            settlementStatus = settlement.getStatus().name();
        }
        
        return "Transfer: " + process.getTransferId()
                + "\nAccount: " + process.getAccountId()
                + "\nAmount: " + process.getAmount()
                + " " + process.getSourceCurrency()
                + "\nConverted amount: " + process.getConvertedAmount()
                + " " + process.getTargetCurrency()
                + "\nStatus: " + process.getStatus()
                + "\nHold: " + holdStatus
                + "\nRisk check: " + checkStatus
                + "\nSettlement: " + settlementStatus;
    }
}