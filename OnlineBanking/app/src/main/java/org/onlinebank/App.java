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
        Scanner sc = new Scanner(System.in);
        cli(sc);
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

        try {
            String input = scanner.nextLine().trim();

            if ("1".equals(input)) {
                clientMenu(scanner);
            } 
            else if ("2".equals(input)) {
                adminMenu(scanner);
            } 
            else if ("0".equals(input)) {
                return;
            } 
            else {
                System.out.println("Unknown option.");
            }
        } 

        catch (InsufficientFundsException e) {
            System.out.println("Transfer declined: " + e.getMessage());
        } 
        catch (RuntimeException e) {
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
        System.out.println("5. Start only A (resume later)");
        System.out.println("0. Back");

        String choice = readText(scanner, "Choose: ");

        if ("1".equals(choice)) {
            newTransfer(scanner, false);
        } else if ("2".equals(choice)) {
            TransferProcess process = transfers.resume(readText(scanner, "Transfer ID: "), true);
            System.out.println("Status: " + process.getStatus());
        } else if ("3".equals(choice)) {
            demoTransfer("XZ", true);
        } else if ("4".equals(choice)) {
            demoTransfer("US", false);
        } else if ("5".equals(choice)) {
            newTransfer(scanner, true);
        } else if ("0".equals(choice)) {
            return;
        } else {
            throw new IllegalArgumentException("Unknown option");
        }
    }



    private static void newTransfer(Scanner scanner, boolean stopAfterIntake) {
        String accountId = readText(scanner, "Sender account ID: ");
        BigDecimal amount = new BigDecimal(
                readText(scanner, "Amount in account currency: ")
        );
        String targetCurrency = readText(scanner, "Target currency (KZT/USD): ").toUpperCase();
        String country = readText(scanner, "Destination country: ").toUpperCase();
        String id = "TR-" + UUID.randomUUID();
        System.out.println("Transfer ID: " + id);

        TransferProcess process;

        if (stopAfterIntake) {
            process = transfers.start(id, accountId, amount, targetCurrency, country);
        } else {
            process = transfers.transfer(id, accountId, amount, targetCurrency, country, true);
        }
        System.out.println("Result: " + process.getStatus());
    }
    


    private static void demoTransfer(String country, boolean networkAvailable) {
        String id = "TR-" + UUID.randomUUID();
        System.out.println("Demo: 90000 KZT, country " + country
                + ", network available: " + networkAvailable);
        System.out.println("Transfer ID: " + id);
        TransferProcess process = transfers.transfer(
                id, "DEMO-KZT", new BigDecimal("90000"), "USD", country, networkAvailable
        );
        System.out.println("Result: " + process.getStatus());
    }



    private static void adminMenu(Scanner scanner) {
        System.out.println("\nAdmin");
        System.out.println("1. Account balance");
        System.out.println("2. Inspect transfer");
        System.out.println("0. Back");

        switch (readText(scanner, "Choose: ")) {
            case "1" -> System.out.println(
                    "Balance: " + admin.getBalance(readText(scanner, "Account ID: "))
            );
            case "2" -> System.out.println(
                    admin.inspectTransfer(readText(scanner, "Transfer ID: "))
            );
            case "0" -> { return; }
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
