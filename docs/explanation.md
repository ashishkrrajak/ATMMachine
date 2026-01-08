# ATM Machine System - Low Level Design

## Table of Contents
1. [Problem Statement](#problem-statement)
2. [Requirements](#requirements)
3. [Design Patterns Used](#design-patterns-used)
4. [System Components](#system-components)
5. [State Machine](#state-machine)
6. [Code Walkthrough](#code-walkthrough)
7. [Key Design Decisions](#key-design-decisions)
8. [Extensibility](#extensibility)

---

## Problem Statement

Design an ATM (Automated Teller Machine) system that can:
- Authenticate users via card and PIN
- Provide balance inquiry
- Handle cash withdrawals with denomination management
- Accept cash deposits
- Support fund transfers between accounts
- Print transaction receipts
- Maintain transaction history

---

## Requirements

### Functional Requirements
1. Card insertion and validation
2. PIN verification with lockout after failed attempts
3. Balance inquiry for linked account
4. Cash withdrawal with denomination selection
5. Cash/check deposit
6. Fund transfer between accounts
7. Transaction receipt printing
8. Transaction cancellation at any point

### Non-Functional Requirements
1. Thread-safe account operations
2. Secure PIN handling
3. Reliable cash inventory management
4. Atomic transaction processing
5. Audit trail of all transactions

---

## Design Patterns Used

### 1. State Pattern (Primary)
**Used in:** `ATMStateHandler` interface and implementations

**Why:** The ATM has clearly defined states with different allowed operations in each state.

```java
interface ATMStateHandler {
    void insertCard(ATM atm, Card card);
    void enterPin(ATM atm, String pin);
    void selectTransaction(ATM atm, TransactionType type);
    void executeTransaction(ATM atm, double amount, String targetAccount);
    void cancel(ATM atm);
}
```

**States:**
| State | Description | Allowed Actions |
|-------|-------------|-----------------|
| `IdleStateHandler` | Waiting for card | Insert card |
| `CardInsertedStateHandler` | Card inserted | Enter PIN, Cancel |
| `PinVerifiedStateHandler` | Authenticated | Select transaction, Cancel |
| `TransactionSelectedStateHandler` | Transaction chosen | Execute, Change selection, Cancel |
| `ProcessingStateHandler` | Executing transaction | None (wait) |

### 2. Singleton Pattern
**Used in:** `ATM` class

**Why:** A physical ATM is a single entity that must maintain consistent state.

```java
public static synchronized ATM getInstance(String atmId, String location) {
    if (instance == null) {
        instance = new ATM(atmId, location);
    }
    return instance;
}
```

### 3. Composition
**Used in:** ATM components

**Why:** ATM is composed of multiple hardware components that work together.

```java
class ATM {
    private CardReader cardReader;
    private CashDispenser cashDispenser;
    private DepositSlot depositSlot;
    private ReceiptPrinter receiptPrinter;
    private BankService bankService;
}
```

---

## System Components

### Core Components

| Component | Responsibility |
|-----------|---------------|
| `ATM` | Main controller, orchestrates all operations |
| `CardReader` | Handles card insertion and ejection |
| `CashDispenser` | Manages cash inventory and dispensing |
| `DepositSlot` | Accepts cash/check deposits |
| `ReceiptPrinter` | Prints transaction receipts |
| `BankService` | Interfaces with bank accounts |

### Domain Entities

| Entity | Responsibility |
|--------|---------------|
| `BankAccount` | Account with balance and PIN |
| `Card` | ATM/Debit card linked to account |
| `Transaction` | Record of ATM transaction |

### Component Details

**CardReader:**
```java
class CardReader {
    private Card currentCard;
    private boolean cardInserted;

    public boolean insertCard(Card card);  // Validates and holds card
    public Card ejectCard();               // Returns and releases card
}
```

**CashDispenser:**
```java
class CashDispenser {
    private Map<Integer, Integer> cashInventory; // denomination -> count
    private static final int[] DENOMINATIONS = {100, 50, 20, 10};

    public boolean canDispense(double amount);        // Check availability
    public Map<Integer, Integer> dispense(double amount); // Dispense cash
}
```

---

## State Machine

### State Diagram

```
    +--------+  insertCard   +-----------------+
    |  IDLE  |-------------->| CARD_INSERTED   |
    +--------+               +-----------------+
        ^                          |
        |                          | enterPin (valid)
        | ejectCard                v
        |                    +-----------------+
        +--------------------| PIN_VERIFIED    |<--+
        |                    +-----------------+   |
        |                          |               |
        | cancel                   | selectTransaction
        |                          v               |
        |                    +-----------------+   |
        +--------------------| TXN_SELECTED    |---+
        |                    +-----------------+   complete
        |                          |               |
        |                          | execute       |
        |                          v               |
        |                    +-----------------+   |
        +--------------------| PROCESSING      |---+
                             +-----------------+
```

### State Transitions Table

| Current State | Action | Next State | Condition |
|---------------|--------|------------|-----------|
| IDLE | insertCard | CARD_INSERTED | Valid, non-expired card |
| CARD_INSERTED | enterPin | PIN_VERIFIED | Correct PIN |
| CARD_INSERTED | enterPin | IDLE | 3 wrong attempts |
| CARD_INSERTED | cancel | IDLE | - |
| PIN_VERIFIED | selectTransaction | TXN_SELECTED | - |
| PIN_VERIFIED | cancel | IDLE | Card ejected |
| TXN_SELECTED | executeTransaction | PROCESSING | - |
| TXN_SELECTED | cancel | PIN_VERIFIED | - |
| PROCESSING | complete | PIN_VERIFIED | Transaction done |

---

## Code Walkthrough

### 1. Card Insertion and PIN Verification

```java
// Insert card
atm.insertCard(card);

// IdleStateHandler handles it
public void insertCard(ATM atm, Card card) {
    if (atm.getCardReader().insertCard(card)) {
        atm.setCurrentCard(card);
        atm.setStateHandler(new CardInsertedStateHandler());
    }
}

// Enter PIN
atm.enterPin("1234");

// CardInsertedStateHandler handles it
public void enterPin(ATM atm, String pin) {
    BankAccount account = atm.getBankService().getAccount(
        atm.getCurrentCard().getAccountNumber());

    if (account.validatePin(pin)) {
        atm.setCurrentAccount(account);
        atm.setStateHandler(new PinVerifiedStateHandler());
    } else if (account.isLocked()) {
        atm.ejectCard();
        atm.setStateHandler(new IdleStateHandler());
    }
}
```

### 2. Cash Withdrawal Process

```java
// Select withdrawal
atm.selectTransaction(TransactionType.WITHDRAWAL);
atm.executeTransaction(280);

// ProcessingStateHandler handles it
public void processTransaction(ATM atm, double amount, String target) {
    // Check if ATM can dispense this amount
    if (!atm.getCashDispenser().canDispense(amount)) {
        System.out.println("Cannot dispense this amount.");
        return;
    }

    // Check and debit account
    if (account.withdraw(amount)) {
        // Dispense cash
        atm.getCashDispenser().dispense(amount);
        transaction.setStatus(TransactionStatus.SUCCESS);
    }

    // Print receipt
    atm.getReceiptPrinter().printReceipt(transaction, account);
}
```

### 3. Cash Dispensing Algorithm

```java
public Map<Integer, Integer> dispense(double amount) {
    Map<Integer, Integer> dispensed = new HashMap<>();
    int remaining = (int) amount;

    // Greedy algorithm - use largest denominations first
    for (int denom : DENOMINATIONS) { // [100, 50, 20, 10]
        int available = cashInventory.get(denom);
        int needed = remaining / denom;
        int used = Math.min(needed, available);

        if (used > 0) {
            dispensed.put(denom, used);
            cashInventory.put(denom, available - used);
            remaining -= used * denom;
        }
    }

    return dispensed;
}
```

**Example:** Dispensing $280
- $100 x 2 = $200 (remaining: $80)
- $50 x 1 = $50 (remaining: $30)
- $20 x 1 = $20 (remaining: $10)
- $10 x 1 = $10 (remaining: $0)

### 4. Fund Transfer

```java
case TRANSFER:
    BankAccount targetAcc = atm.getBankService().getAccount(targetAccount);

    if (targetAcc == null) {
        System.out.println("Target account not found.");
    } else if (account.withdraw(amount)) {
        targetAcc.deposit(amount);
        success = true;
    }
    break;
```

### 5. Account Lockout

```java
public boolean validatePin(String inputPin) {
    if (isLocked) {
        System.out.println("Account is locked.");
        return false;
    }

    if (this.pin.equals(inputPin)) {
        failedAttempts = 0;
        return true;
    } else {
        failedAttempts++;
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            isLocked = true;
            System.out.println("Account locked.");
        }
        return false;
    }
}
```

---

## Key Design Decisions

### 1. Thread-Safe Account Operations

```java
public synchronized boolean withdraw(double amount) {
    if (amount > balance) return false;
    balance -= amount;
    return true;
}

public synchronized boolean deposit(double amount) {
    if (amount <= 0) return false;
    balance += amount;
    return true;
}
```

**Why:** Multiple ATMs might access the same account simultaneously.

### 2. Greedy Cash Dispensing Algorithm

The algorithm uses largest denominations first:
- Minimizes number of notes dispensed
- Simple and efficient O(D) where D = number of denominations
- May not always find solution even if one exists (limitation)

### 3. State Pattern vs. Enum State

**State Pattern (chosen):**
```java
interface ATMStateHandler {
    void insertCard(ATM atm, Card card);
    void enterPin(ATM atm, String pin);
    // ...
}
```

**Enum State (alternative):**
```java
enum ATMState { IDLE, CARD_INSERTED, PIN_VERIFIED, ... }

void handleAction(ATMState state, Action action) {
    switch(state) {
        case IDLE:
            if (action == INSERT_CARD) { ... }
            break;
        // ... complex switch-case
    }
}
```

**Why State Pattern:**
- Encapsulates state-specific behavior
- Easier to add new states
- No complex switch statements
- Follows Open/Closed Principle

### 4. Component Separation

Each hardware component is a separate class:
- `CardReader`: Physical card slot
- `CashDispenser`: Cash storage and dispensing mechanism
- `DepositSlot`: Deposit mechanism
- `ReceiptPrinter`: Receipt printing hardware

**Benefits:**
- Single Responsibility Principle
- Easy to mock for testing
- Can replace components independently

---

## Extensibility

### Adding New Transaction Types

1. Add to `TransactionType` enum
2. Add handling in `ProcessingStateHandler.processTransaction()`

```java
enum TransactionType {
    BALANCE_INQUIRY, WITHDRAWAL, DEPOSIT, TRANSFER,
    BILL_PAYMENT,  // New
    MOBILE_RECHARGE // New
}

// In ProcessingStateHandler
case BILL_PAYMENT:
    // Process bill payment
    break;
```

### Adding New Card Types

```java
enum CardType {
    DEBIT, CREDIT,
    PREPAID,    // New
    CORPORATE   // New
}

class Card {
    // Add card-specific validation
    public double getWithdrawalLimit() {
        switch(type) {
            case DEBIT: return 1000;
            case CREDIT: return 500;
            case PREPAID: return balance;
            default: return 0;
        }
    }
}
```

### Adding Multi-Language Support

```java
interface DisplayMessage {
    String getWelcome();
    String getInsertCard();
    String getEnterPin();
    // ...
}

class EnglishMessages implements DisplayMessage {
    public String getWelcome() { return "Welcome to ATM"; }
}

class SpanishMessages implements DisplayMessage {
    public String getWelcome() { return "Bienvenido al cajero"; }
}
```

### Adding Transaction Limits

```java
class WithdrawalLimiter {
    private double dailyLimit = 2000;
    private Map<String, Double> dailyWithdrawals = new HashMap<>();

    public boolean canWithdraw(String accountNumber, double amount) {
        double withdrawn = dailyWithdrawals.getOrDefault(accountNumber, 0.0);
        return (withdrawn + amount) <= dailyLimit;
    }
}
```

---

## Time and Space Complexity

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Insert Card | O(1) | O(1) |
| Validate PIN | O(1) | O(1) |
| Balance Inquiry | O(1) | O(1) |
| Withdrawal | O(D) | O(D) |
| Deposit | O(1) | O(1) |
| Transfer | O(1) | O(1) |

Where D = number of denominations

---

## Security Considerations

1. **PIN Storage:** Should be hashed (simplified here for demo)
2. **PIN Transmission:** Should be encrypted
3. **Session Timeout:** Auto-eject card after inactivity
4. **Audit Logging:** Log all transactions for security
5. **Card Skimming Prevention:** Hardware security measures

```java
// Example: Hashed PIN verification
public boolean validatePin(String inputPin) {
    String hashedInput = hashFunction(inputPin);
    return this.hashedPin.equals(hashedInput);
}
```

---

## Interview Tips

1. **Start with Requirements:** Clarify transaction types, limits
2. **Identify Components:** Card reader, dispenser, printer
3. **Draw State Diagram:** Essential for ATM design
4. **Consider Concurrency:** Multiple accounts, thread safety
5. **Discuss Edge Cases:**
   - Card stuck in reader
   - Power failure during transaction
   - Cash dispenser jam
   - Network failure with bank
6. **Security:** PIN handling, audit trails

---

## Potential Improvements

1. **Network Layer:** Add bank network communication
2. **Session Management:** Timeout and session tokens
3. **Denomination Optimization:** Dynamic programming for optimal dispensing
4. **Biometric Auth:** Fingerprint/face recognition
5. **Cardless Transactions:** Mobile/QR code based
6. **Real-time Monitoring:** Admin dashboard for cash levels

---

## References
- Design Patterns: Elements of Reusable Object-Oriented Software (Gang of Four)
- Head First Design Patterns
- Clean Architecture by Robert C. Martin
