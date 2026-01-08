package atmmachine.models;

import atmmachine.enums.TransactionType;
import atmmachine.enums.TransactionStatus;
import java.util.Date;

public class Transaction {
    private String transactionId;
    private TransactionType type;
    private double amount;
    private String sourceAccount;
    private String targetAccount;
    private Date timestamp;
    private TransactionStatus status;
    private String description;

    public Transaction(String transactionId, TransactionType type, double amount,
                       String sourceAccount, String targetAccount) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.timestamp = new Date();
        this.status = TransactionStatus.PENDING;
    }

    public void setStatus(TransactionStatus status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }

    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getSourceAccount() { return sourceAccount; }
    public String getTargetAccount() { return targetAccount; }
    public Date getTimestamp() { return timestamp; }
    public TransactionStatus getStatus() { return status; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("Transaction[%s] %s: $%.2f - %s",
            transactionId, type, amount, status);
    }
}
