package hardware.cardreader;

import java.time.YearMonth;

import card.Card;
import card.CardValidator;

public class SimpleCardReader implements ICardReader {
    @Override
    public Card readCard() {
        // In real life, this would interact with hardware to read the card details (magnetic/chip data).
        System.out.println("Reading card from SimpleCardReader...");
        YearMonth expiry = YearMonth.of(2025, 12);
        Card card = new Card("4539-1488-0343-6467", expiry);

        if (!CardValidator.validate(card)) {
            throw new IllegalArgumentException("Invalid card read!");
        }

        return card;
    }

    @Override
    public void ejectCard() {
        // In real life, this would interact with hardware to eject the card.
        System.out.println("Ejecting card from SimpleCardReader...");
    }

}
