package state;

import card.Card;
import enums.TransactionType;

/**
 * ATM-specific state interface — includes lifecycle hooks, user actions and internal actions.
 */
public interface IATMState {
    // lifecycle
    void onEnter();
    void onExit();

    // Card operations
    void insertCard(Card card);
    void ejectCard();

    // User input operations
    void enterPIN(String pin);
    void selectTransaction(TransactionType type);
    void enterAmount(double amount);
    void cancel();

    // Internal system operations (called by ATM/core)
    void processTransaction();
    void dispenseCash();
    void printReceipt();
}
