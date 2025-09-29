package card;

import java.time.YearMonth;

/*
 * Immutable class representing a Card
 * - All fields are private and final
 * - No setters, only constructor injection (deep copies for mutable fields, if any)
 * - Getters (deep copies for mutable fields, if any)
 * - Marks the class as final to prevent subclassing
 */
public final class Card {
    private final String cardNumber;
    private final YearMonth expiryDate;

    public Card(String cardNumber, YearMonth expiryDate) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
    }

    public String getCardNumber() { return cardNumber; }
    public YearMonth getExpiryDate() { return expiryDate; }
}
