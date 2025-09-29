package bank;

import card.Card;

public class BankServiceProxy implements IBankService {
    private final IBankService real;

    public BankServiceProxy(IBankService bankService) {
        this.real = bankService;
    }

    @Override
    public boolean validatePIN(Card card, String pin) {
        System.out.println("[BANK PROXY] validatePIN for " + card.getCardNumber());
        return real.validatePIN(card, pin);
    }

    @Override
    public boolean hasSufficientBalance(Card card, int amount) {
        System.out.println("[BANK PROXY] hasSufficientBalance for " + card.getCardNumber() + " for amount: " + amount);
        return real.hasSufficientBalance(card, amount);
    }

    @Override
    public boolean hasNotExceededLimit(Card card, int amount) {
        System.out.println("[BANK PROXY] hasNotExceededLimit for " + card.getCardNumber() + " for amount: " + amount);
        return real.hasNotExceededLimit(card, amount);
    }

    @Override
    public boolean debitAccount(Card card, int amount) {
        System.out.println("[BANK PROXY] debitAccount for " + card.getCardNumber() + " for amount: " + amount);
        return real.debitAccount(card, amount);
    }
    
}
