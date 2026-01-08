package atmmachine.states;

import atmmachine.ATM;
import atmmachine.enums.TransactionType;
import atmmachine.models.BankAccount;
import atmmachine.models.Card;

public class CardInsertedStateHandler implements ATMStateHandler {
    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("Card already inserted.");
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        BankAccount account = atm.getBankService().getAccount(atm.getCurrentCard().getAccountNumber());

        if (account == null) {
            System.out.println("Account not found.");
            atm.ejectCard();
            atm.setStateHandler(new IdleStateHandler());
            return;
        }

        if (account.validatePin(pin)) {
            atm.setCurrentAccount(account);
            System.out.println("PIN verified. Welcome, " + account.getHolderName() + "!");
            atm.setStateHandler(new PinVerifiedStateHandler());
        } else if (account.isLocked()) {
            atm.ejectCard();
            atm.setStateHandler(new IdleStateHandler());
        }
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        System.out.println("Please enter your PIN first.");
    }

    @Override
    public void executeTransaction(ATM atm, double amount, String targetAccount) {
        System.out.println("Please enter your PIN first.");
    }

    @Override
    public void cancel(ATM atm) {
        atm.ejectCard();
        atm.setStateHandler(new IdleStateHandler());
    }

    @Override
    public String getStateName() { return "CARD_INSERTED"; }
}
