package org.onlinebank;

import org.onlinebank.exceptions.InsufficientFundsException;
import org.onlinebank.facade.BankOperations;
import org.onlinebank.facade.TransferFacade;
import org.onlinebank.facade.TransferProcess;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

public class App {

    private static final TransferFacade transfers = new TransferFacade();
    private static final BankOperations admin = new BankOperations();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            cli(scanner);
        }
    }

    private static void cli(Scanner scanner) {
        while (true) {
            System.out.println("\nOnline Banking");
            System.out.println("1. Client menu");
            System.out.println("2. Admin menu");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            if (!scanner.hasNextLine()) {
                return;
            }

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> clientMenu(scanner);
                    case "2" -> adminMenu(scanner);
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Unknown option.");
                }
            } catch (InsufficientFundsException e) {
                System.out.println("Transfer declined: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void clientMenu(Scanner scanner) {
        System.out.println("\nClient");
        System.out.println("1. New transfer");
        System.out.println("2. Resume transfer");
        System.out.println("3. Demo: B failure");
        System.out.println("4. Demo: C failure");
        System.out.println("0. Back");

        String accountId = "DEMO-KZT";
        BigDecimal amount = new BigDecimal("90000");
        String targetCurrency = "USD";
        String country = "US";
        boolean networkAvailable = true;

        switch (readText(scanner, "Choose: ")) {
            case "1" -> {
                accountId = readText(scanner, "Sender account ID: ");

                amount = new BigDecimal(
                        readText(scanner, "Amount in account currency: ")
                );

                targetCurrency = readText(
                        scanner, "Target currency (KZT/USD): "
                ).toUpperCase();

                country = readText(
                        scanner, "Destination country: "
                ).toUpperCase();
            }

            case "2" -> {
                String id = readText(scanner, "Transfer ID: ");

                TransferProcess process = transfers.resume(id, true);

                System.out.println("Status: " + process.getStatus());
                return;
            }

            case "3" -> {
                System.out.println(
                        "Demo: 90000 KZT from DEMO-KZT, restricted country XZ."
                );

                country = "XZ";
            }

            case "4" -> {
                System.out.println(
                        "Demo: 90000 KZT from DEMO-KZT, settlement network unavailable."
                );

                networkAvailable = false;
            }

            case "0" -> {
                return;
            }

            default -> throw new IllegalArgumentException("Unknown option");
        }

        String id = "TR-" + UUID.randomUUID();

        System.out.println("Transfer ID: " + id);

        TransferProcess process = transfers.transfer(
                id,
                accountId,
                amount,
                targetCurrency,
                country,
                networkAvailable
        );

        System.out.println("Result: " + process.getStatus());
    }

    private static void adminMenu(Scanner scanner) {
        System.out.println("\nAdmin");
        System.out.println("1. Account balance");
        System.out.println("2. Inspect transfer");
        System.out.println("0. Back");

        switch (readText(scanner, "Choose: ")) {
            case "1" -> {
                String accountId = readText(scanner, "Account ID: ");

                System.out.println(
                        "Balance: " + admin.getBalance(accountId)
                );
            }

            case "2" -> {
                String id = readText(scanner, "Transfer ID: ");

                System.out.println(admin.inspectTransfer(id));
            }

            case "0" -> {
                return;
            }

            default -> throw new IllegalArgumentException("Unknown option");
        }
    }

    
    private static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);

        if (!scanner.hasNextLine()) {
            throw new IllegalArgumentException("No input provided");
        }

        return scanner.nextLine().trim();
    }
}
