package atmmachine.models;

import atmmachine.enums.CardType;
import java.util.Date;

public class Card {
    private String cardNumber;
    private String holderName;
    private CardType type;
    private Date expiryDate;
    private String accountNumber;

    public Card(String cardNumber, String holderName, CardType type, Date expiryDate, String accountNumber) {
        this.cardNumber = cardNumber;
        this.holderName = holderName;
        this.type = type;
        this.expiryDate = expiryDate;
        this.accountNumber = accountNumber;
    }

    public boolean isExpired() {
        return new Date().after(expiryDate);
    }

    public String getCardNumber() { return cardNumber; }
    public String getHolderName() { return holderName; }
    public CardType getType() { return type; }
    public Date getExpiryDate() { return expiryDate; }
    public String getAccountNumber() { return accountNumber; }

    public String getMaskedCardNumber() {
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
