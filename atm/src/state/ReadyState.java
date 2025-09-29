package state;

import core.ATM;

public class ReadyState implements IATMState{
    private final ATM atm;

    public ReadyState(ATM atm) {
        this.atm = atm;
    }

    @Override
    public void onEnter() {
        atm.getScreen().showMessage("[READY STATE] Welcome. Please insert your card.");
    }

    @Override
    public void onExit() { /* no-op */ }

    @Override
    public void insertCard(card.Card card) {
        System.out.println("[STATE] Card inserted. Transitioning to CardInsertedState.");
        atm.setCurrentCard(atm.getCardReader().readCard());
        atm.setCurrentState(ATMStateFactory.getState("CardInsertedState", atm));
    }   

    @Override
    public void ejectCard() {
        System.out.println("[STATE] No card to eject.");
    }

    @Override
    public void enterPIN(String pin) {
        System.out.println("[STATE] No card inserted. Cannot enter PIN.");
    }

    @Override
    public void selectTransaction(enums.TransactionType type) {
        System.out.println("[STATE] No card inserted. Cannot select transaction.");
    }

    @Override
    public void enterAmount(double amount) {
        System.out.println("[STATE] No card inserted. Cannot enter amount.");
    }

    @Override
    public void cancel() {
        System.out.println("[STATE] No operation to cancel.");
    }

    @Override
    public void processTransaction() {
        System.out.println("[STATE] No transaction to process.");
    }

    @Override
    public void dispenseCash() {
        System.out.println("[STATE] No cash to dispense.");
    }

    @Override
    public void printReceipt() {
        System.out.println("[STATE] No receipt to print.");
    }
    
}
