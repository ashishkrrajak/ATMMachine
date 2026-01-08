package atmmachine;

import atmmachine.components.CardReader;
import atmmachine.components.CashDispenser;
import atmmachine.components.DepositSlot;
import atmmachine.components.ReceiptPrinter;
import atmmachine.enums.TransactionType;
import atmmachine.models.BankAccount;
import atmmachine.models.Card;
import atmmachine.models.Transaction;
import atmmachine.services.BankService;
import atmmachine.states.ATMStateHandler;
import atmmachine.states.IdleStateHandler;

import java.util.ArrayList;
import java.util.List;

public class ATM {
    private static ATM instance;

    private String atmId;
    private String location;
    private CardReader cardReader;
    private CashDispenser cashDispenser;
    private DepositSlot depositSlot;
    private ReceiptPrinter receiptPrinter;
    private BankService bankService;

    private ATMStateHandler stateHandler;
    private Card currentCard;
    private BankAccount currentAccount;
    private TransactionType selectedTransactionType;
    private List<Transaction> transactionHistory;

    private ATM(String atmId, String location) {
        this.atmId = atmId;
        this.location = location;
        this.cardReader = new CardReader();
        this.cashDispenser = new CashDispenser();
        this.depositSlot = new DepositSlot();
        this.receiptPrinter = new ReceiptPrinter();
        this.bankService = new BankService();
        this.stateHandler = new IdleStateHandler();
        this.transactionHistory = new ArrayList<>();
    }

    public static synchronized ATM getInstance(String atmId, String location) {
        if (instance == null) {
            instance = new ATM(atmId, location);
        }
        return instance;
    }

    public static synchronized ATM getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ATM not initialized.");
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    // State pattern delegation
    public void insertCard(Card card) {
        stateHandler.insertCard(this, card);
    }

    public void enterPin(String pin) {
        stateHandler.enterPin(this, pin);
    }

    public void selectTransaction(TransactionType type) {
        stateHandler.selectTransaction(this, type);
    }

    public void executeTransaction(double amount, String targetAccount) {
        stateHandler.executeTransaction(this, amount, targetAccount);
    }

    public void executeTransaction(double amount) {
        executeTransaction(amount, null);
    }

    public void cancel() {
        stateHandler.cancel(this);
    }

    public void ejectCard() {
        cardReader.ejectCard();
        currentCard = null;
        currentAccount = null;
        selectedTransactionType = null;
    }

    // Internal setters used by state handlers
    public void setStateHandler(ATMStateHandler handler) { this.stateHandler = handler; }
    public void setCurrentCard(Card card) { this.currentCard = card; }
    public void setCurrentAccount(BankAccount account) { this.currentAccount = account; }
    public void setSelectedTransactionType(TransactionType type) { this.selectedTransactionType = type; }
    public void addTransaction(Transaction transaction) { this.transactionHistory.add(transaction); }

    // Getters
    public String getAtmId() { return atmId; }
    public String getLocation() { return location; }
    public CardReader getCardReader() { return cardReader; }
    public CashDispenser getCashDispenser() { return cashDispenser; }
    public DepositSlot getDepositSlot() { return depositSlot; }
    public ReceiptPrinter getReceiptPrinter() { return receiptPrinter; }
    public BankService getBankService() { return bankService; }
    public ATMStateHandler getStateHandler() { return stateHandler; }
    public Card getCurrentCard() { return currentCard; }
    public BankAccount getCurrentAccount() { return currentAccount; }
    public TransactionType getSelectedTransactionType() { return selectedTransactionType; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }

    public void displayStatus() {
        System.out.println("\n========== ATM STATUS ==========");
        System.out.println("ATM ID: " + atmId);
        System.out.println("Location: " + location);
        System.out.println("Current State: " + stateHandler.getStateName());
        if (currentCard != null) {
            System.out.println("Current Card: " + currentCard.getMaskedCardNumber());
        }
        if (currentAccount != null) {
            System.out.println("Account: " + currentAccount.getAccountNumber());
        }
        System.out.println("Total Transactions: " + transactionHistory.size());
        System.out.println("=================================\n");
    }
}
