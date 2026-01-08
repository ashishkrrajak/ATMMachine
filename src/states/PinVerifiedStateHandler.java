package atmmachine.states;

import atmmachine.ATM;
import atmmachine.enums.TransactionType;
import atmmachine.models.Card;

public class PinVerifiedStateHandler implements ATMStateHandler {
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
        System.out.println("Selected transaction: " + type);
        atm.setStateHandler(new TransactionSelectedStateHandler());
    }

    @Override
    public void executeTransaction(ATM atm, double amount, String targetAccount) {
        System.out.println("Please select a transaction type first.");
    }

    @Override
    public void cancel(ATM atm) {
        atm.ejectCard();
        atm.setStateHandler(new IdleStateHandler());
    }

    @Override
    public String getStateName() { return "PIN_VERIFIED"; }
}
