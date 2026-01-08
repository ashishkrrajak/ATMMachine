package atmmachine.components;

import atmmachine.models.Card;

public class CardReader {
    private Card currentCard;
    private boolean cardInserted;

    public boolean insertCard(Card card) {
        if (cardInserted) {
            System.out.println("A card is already inserted.");
            return false;
        }

        if (card.isExpired()) {
            System.out.println("Card is expired.");
            return false;
        }

        this.currentCard = card;
        this.cardInserted = true;
        System.out.println("Card inserted: " + card.getMaskedCardNumber());
        return true;
    }

    public Card ejectCard() {
        Card ejected = this.currentCard;
        this.currentCard = null;
        this.cardInserted = false;
        if (ejected != null) {
            System.out.println("Card ejected: " + ejected.getMaskedCardNumber());
        }
        return ejected;
    }

    public Card getCurrentCard() { return currentCard; }
    public boolean isCardInserted() { return cardInserted; }
}
