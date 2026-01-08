package atmmachine.states;

import atmmachine.ATM;
import atmmachine.enums.TransactionStatus;
import atmmachine.enums.TransactionType;
import atmmachine.models.BankAccount;
import atmmachine.models.Card;
import atmmachine.models.Transaction;

public class ProcessingStateHandler implements ATMStateHandler {
    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("Transaction in progress. Please wait.");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        System.out.println("Transaction in progress. Please wait.");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        System.out.println("Transaction in progress. Please wait.");
    }

    @Override
    public void executeTransaction(ATM atm, double amount, String targetAccount) {
        System.out.println("Transaction in progress. Please wait.");
    }

    @Override
    public void cancel(ATM atm) {
        System.out.println("Cannot cancel. Transaction in progress.");
    }

    public void processTransaction(ATM atm, double amount, String targetAccount) {
        TransactionType type = atm.getSelectedTransactionType();
        BankAccount account = atm.getCurrentAccount();

        String transactionId = "TXN" + System.currentTimeMillis();
        Transaction transaction = new Transaction(transactionId, type, amount,
            account.getAccountNumber(), targetAccount);

        boolean success = false;

        switch (type) {
            case BALANCE_INQUIRY:
                System.out.println("\nBalance for account " + account.getAccountNumber() +
                    ": $" + String.format("%.2f", account.getBalance()));
                success = true;
                break;

            case WITHDRAWAL:
                if (!atm.getCashDispenser().canDispense(amount)) {
                    System.out.println("ATM cannot dispense this amount. Try a different amount.");
                } else if (account.withdraw(amount)) {
                    atm.getCashDispenser().dispense(amount);
                    success = true;
                }
                break;

            case DEPOSIT:
                atm.getDepositSlot().acceptCash(amount);
                if (account.deposit(amount)) {
                    success = true;
                }
                atm.getDepositSlot().reset();
                break;

            case TRANSFER:
                BankAccount targetAcc = atm.getBankService().getAccount(targetAccount);
                if (targetAcc == null) {
                    System.out.println("Target account not found.");
                } else if (account.withdraw(amount)) {
                    targetAcc.deposit(amount);
                    success = true;
                    System.out.println("Transferred $" + String.format("%.2f", amount) +
                        " to account " + targetAccount);
                }
                break;
        }

        transaction.setStatus(success ? TransactionStatus.SUCCESS : TransactionStatus.FAILED);
        atm.addTransaction(transaction);

        if (success) {
            atm.getReceiptPrinter().printReceipt(transaction, account);
        }

        // Ask if user wants another transaction
        System.out.println("\nWould you like to perform another transaction? (Returning to menu)");
        atm.setStateHandler(new PinVerifiedStateHandler());
    }

    @Override
    public String getStateName() { return "PROCESSING"; }
}
