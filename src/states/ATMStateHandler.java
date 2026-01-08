package atmmachine.states;

import atmmachine.ATM;
import atmmachine.enums.TransactionType;
import atmmachine.models.Card;

public interface ATMStateHandler {
    void insertCard(ATM atm, Card card);
    void enterPin(ATM atm, String pin);
    void selectTransaction(ATM atm, TransactionType type);
    void executeTransaction(ATM atm, double amount, String targetAccount);
    void cancel(ATM atm);
    String getStateName();
}
