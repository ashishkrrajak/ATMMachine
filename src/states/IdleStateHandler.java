package atmmachine.states;

import atmmachine.ATM;
import atmmachine.enums.TransactionType;
import atmmachine.models.Card;

public class IdleStateHandler implements ATMStateHandler {
    @Override
    public void insertCard(ATM atm, Card card) {
        if (atm.getCardReader().insertCard(card)) {
            atm.setCurrentCard(card);
            atm.setStateHandler(new CardInsertedStateHandler());
        }
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        System.out.println("Please insert your card first.");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        System.out.println("Please insert your card first.");
    }

    @Override
    public void executeTransaction(ATM atm, double amount, String targetAccount) {
        System.out.println("Please insert your card first.");
    }

    @Override
    public void cancel(ATM atm) {
        System.out.println("No transaction in progress.");
    }

    @Override
    public String getStateName() { return "IDLE"; }
}
