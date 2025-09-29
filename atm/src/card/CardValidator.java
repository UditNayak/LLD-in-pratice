package card;

import java.time.YearMonth;

public class CardValidator {

    /**
     * Checks if the card number is valid using the Luhn algorithm.
     */
    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) return false;

        String digitsOnly = cardNumber.replaceAll("\\D", ""); // Remove non-digit chars
        int sum = 0;
        boolean alternate = false;

        for (int i = digitsOnly.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(digitsOnly.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    /**
     * Checks if the card expiry date is valid (not in the past).
     */
    public static boolean isValidExpiryDate(YearMonth expiryDate) {
        if (expiryDate == null) return false;

        YearMonth now = YearMonth.now();
        return !expiryDate.isBefore(now);
    }

    /**
     * Validates the entire card object.
     */
    public static boolean validate(Card card) {
        if (card == null) return false;
        return isValidCardNumber(card.getCardNumber()) && isValidExpiryDate(card.getExpiryDate());
    }
}
