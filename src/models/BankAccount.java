package atmmachine.models;

public class BankAccount {
    private String accountNumber;
    private String holderName;
    private double balance;
    private String pin;
    private boolean isLocked;
    private int failedAttempts;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    public BankAccount(String accountNumber, String holderName, double initialBalance, String pin) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialBalance;
        this.pin = pin;
        this.isLocked = false;
        this.failedAttempts = 0;
    }

    public boolean validatePin(String inputPin) {
        if (isLocked) {
            System.out.println("Account is locked. Please contact bank.");
            return false;
        }

        if (this.pin.equals(inputPin)) {
            failedAttempts = 0;
            return true;
        } else {
            failedAttempts++;
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                isLocked = true;
                System.out.println("Account locked due to too many failed attempts.");
            } else {
                System.out.println("Invalid PIN. " + (MAX_FAILED_ATTEMPTS - failedAttempts) + " attempts remaining.");
            }
            return false;
        }
    }

    public synchronized boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
            return false;
        }
        if (amount > balance) {
            System.out.println("Insufficient funds. Available balance: $" + String.format("%.2f", balance));
            return false;
        }
        balance -= amount;
        return true;
    }

    public synchronized boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount.");
            return false;
        }
        balance += amount;
        return true;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public boolean isLocked() { return isLocked; }
}
