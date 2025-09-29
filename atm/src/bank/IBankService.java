package bank;

import card.Card;

public interface IBankService {
    boolean validatePIN(Card card, String pin);
    boolean hasSufficientBalance(Card card, int amount);
    boolean hasNotExceededLimit(Card card, int amount);
    boolean debitAccount(Card card, int amount);

    // In future, we can have methods like
    // boolean creditAccount(Card card, int amount);
    // int getAccountBalance(Card card);
    // List<String> getMiniStatement(Card card);
}
