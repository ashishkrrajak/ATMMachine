package atmmachine;

import atmmachine.enums.CardType;
import atmmachine.enums.TransactionType;
import atmmachine.models.BankAccount;
import atmmachine.models.Card;
import atmmachine.models.Transaction;

import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("        ATM MACHINE LOW LEVEL DESIGN DEMO");
        System.out.println("=================================================\n");

        // Create ATM
        ATM atm = ATM.getInstance("ATM-001", "123 Main Street, City Center");

        // Setup bank accounts
        BankAccount account1 = new BankAccount("ACC001", "John Doe", 5000.00, "1234");
        BankAccount account2 = new BankAccount("ACC002", "Jane Smith", 3000.00, "5678");
        BankAccount account3 = new BankAccount("ACC003", "Bob Wilson", 10000.00, "9999");

        atm.getBankService().addAccount(account1);
        atm.getBankService().addAccount(account2);
        atm.getBankService().addAccount(account3);

        // Create cards
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 3);
        Date futureDate = cal.getTime();

        Card card1 = new Card("1234567890123456", "John Doe", CardType.DEBIT, futureDate, "ACC001");
        Card card2 = new Card("9876543210987654", "Jane Smith", CardType.DEBIT, futureDate, "ACC002");

        // Display initial status
        atm.displayStatus();
        atm.getCashDispenser().displayInventory();

        // Demo 1: Balance Inquiry
        System.out.println("=== DEMO 1: Balance Inquiry ===");
        atm.insertCard(card1);
        atm.enterPin("1234");
        atm.selectTransaction(TransactionType.BALANCE_INQUIRY);
        atm.executeTransaction(0);
        atm.cancel(); // Return to menu
        atm.ejectCard();

        atm.displayStatus();

        // Demo 2: Cash Withdrawal
        System.out.println("\n=== DEMO 2: Cash Withdrawal ===");
        atm.insertCard(card1);
        atm.enterPin("1234");
        atm.selectTransaction(TransactionType.WITHDRAWAL);
        atm.executeTransaction(280);
        atm.cancel();
        atm.ejectCard();

        atm.displayStatus();

        // Demo 3: Cash Deposit
        System.out.println("\n=== DEMO 3: Cash Deposit ===");
        atm.insertCard(card2);
        atm.enterPin("5678");
        atm.selectTransaction(TransactionType.DEPOSIT);
        atm.executeTransaction(500);
        atm.cancel();
        atm.ejectCard();

        atm.displayStatus();

        // Demo 4: Fund Transfer
        System.out.println("\n=== DEMO 4: Fund Transfer ===");
        atm.insertCard(card1);
        atm.enterPin("1234");
        atm.selectTransaction(TransactionType.TRANSFER);
        atm.executeTransaction(200, "ACC002"); // Transfer to Jane's account
        atm.cancel();
        atm.ejectCard();

        // Demo 5: Invalid PIN (Account Lockout)
        System.out.println("\n=== DEMO 5: Invalid PIN Attempts ===");
        atm.insertCard(card2);
        atm.enterPin("0000"); // Wrong PIN
        atm.enterPin("1111"); // Wrong PIN
        atm.enterPin("2222"); // Wrong PIN - Account should lock
        atm.cancel();

        // Demo 6: Insufficient Funds
        System.out.println("\n=== DEMO 6: Insufficient Funds ===");
        atm.insertCard(card1);
        atm.enterPin("1234");
        atm.selectTransaction(TransactionType.WITHDRAWAL);
        atm.executeTransaction(50000); // More than balance
        atm.cancel();
        atm.ejectCard();

        // Demo 7: Transaction Cancellation
        System.out.println("\n=== DEMO 7: Transaction Cancellation ===");
        atm.insertCard(card1);
        atm.enterPin("1234");
        atm.selectTransaction(TransactionType.WITHDRAWAL);
        System.out.println("User decides to cancel...");
        atm.cancel();
        atm.ejectCard();

        // Final status
        System.out.println("\n=== FINAL STATUS ===");
        atm.displayStatus();
        atm.getCashDispenser().displayInventory();

        System.out.println("\n=== Transaction History ===");
        for (Transaction t : atm.getTransactionHistory()) {
            System.out.println(t);
        }

        System.out.println("\n=================================================");
        System.out.println("                DEMO COMPLETED");
        System.out.println("=================================================");
    }
}
