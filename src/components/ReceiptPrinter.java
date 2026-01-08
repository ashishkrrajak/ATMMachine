package atmmachine.components;

import atmmachine.models.BankAccount;
import atmmachine.models.Transaction;

public class ReceiptPrinter {
    public void printReceipt(Transaction transaction, BankAccount account) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("           TRANSACTION RECEIPT");
        System.out.println("=".repeat(40));
        System.out.println("Date: " + transaction.getTimestamp());
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Type: " + transaction.getType());
        System.out.println("-".repeat(40));

        switch (transaction.getType()) {
            case BALANCE_INQUIRY:
                System.out.println("Current Balance: $" + String.format("%.2f", account.getBalance()));
                break;
            case WITHDRAWAL:
                System.out.println("Amount Withdrawn: $" + String.format("%.2f", transaction.getAmount()));
                System.out.println("Remaining Balance: $" + String.format("%.2f", account.getBalance()));
                break;
            case DEPOSIT:
                System.out.println("Amount Deposited: $" + String.format("%.2f", transaction.getAmount()));
                System.out.println("New Balance: $" + String.format("%.2f", account.getBalance()));
                break;
            case TRANSFER:
                System.out.println("Amount Transferred: $" + String.format("%.2f", transaction.getAmount()));
                System.out.println("To Account: " + transaction.getTargetAccount());
                System.out.println("Remaining Balance: $" + String.format("%.2f", account.getBalance()));
                break;
        }

        System.out.println("-".repeat(40));
        System.out.println("Status: " + transaction.getStatus());
        System.out.println("=".repeat(40));
        System.out.println("Thank you for using our ATM!");
        System.out.println("=".repeat(40) + "\n");
    }
}
