package bank;

import card.Card;

public class BankServiceImpl implements IBankService {
    /*
     * This Service will interact with Bank's backend systems.
     * Suppose It is a HDFC Bank ATM, it will interact with HDFC's banking systems.
     * Here, we are simulating the interactions.
     * In real-world, this would involve secure API calls, error handling, logging, etc
     */
    @Override
    public boolean validatePIN(Card card, String pin) {
        return true; 
    }

    @Override
    public boolean hasSufficientBalance(Card card, int amount) {
        return true;
    }

    @Override
    public boolean hasNotExceededLimit(Card card, int amount) {
        return false;
    }

    @Override
    public boolean debitAccount(Card card, int amount) {
        return true;
    }
    
}
