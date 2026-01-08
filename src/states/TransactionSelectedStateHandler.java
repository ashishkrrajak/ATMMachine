package atmmachine.states;

import atmmachine.ATM;
import atmmachine.enums.TransactionType;
import atmmachine.models.Card;

public class TransactionSelectedStateHandler implements ATMStateHandler {
    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("Card already inserted.");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        System.out.println("PIN already verified.");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        atm.setSelectedTransactionType(type);
        System.out.println("Changed transaction type to: " + type);
    }

    @Override
    public void executeTransaction(ATM atm, double amount, String targetAccount) {
        atm.setStateHandler(new ProcessingStateHandler());
        ((ProcessingStateHandler) atm.getStateHandler()).processTransaction(atm, amount, targetAccount);
    }

    @Override
    public void cancel(ATM atm) {
        System.out.println("Transaction cancelled.");
        atm.setStateHandler(new PinVerifiedStateHandler());
    }

    @Override
    public String getStateName() { return "TRANSACTION_SELECTED"; }
}
