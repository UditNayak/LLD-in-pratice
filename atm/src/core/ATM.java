package core;

import bank.BankServiceImpl;
import bank.BankServiceProxy;
import bank.IBankService;
import card.Card;
import hardware.Printer;
import hardware.Screen;
import hardware.keypad.IKeyPad;
import state.ATMStateFactory;
import state.IATMState;
import hardware.cardreader.ICardReader;

public class ATM {
    // hardware
    private final ICardReader cardReader;
    private final IKeyPad keyPad;
    private final Screen screen;
    private final Printer printer;
    private final CashDispenser cashDispenser;

    // bank
    private final IBankService bankService;

    // current session
    private Card currentCard;
    private IATMState currentState;

    public ATM(ICardReader cardReader, IKeyPad keyPad, Screen screen, Printer printer) {
        this.cardReader = cardReader;
        this.keyPad = keyPad;
        this.screen = screen;
        this.printer = printer;
        this.cashDispenser = CashDispenser.getInstance();
        this.bankService = new BankServiceProxy(new BankServiceImpl());
        this.currentState = ATMStateFactory.getState("ReadyState", this);
    }

    //getters
    public ICardReader getCardReader() { return cardReader; }
    public IKeyPad getKeyPad() { return keyPad; }
    public Screen getScreen() { return screen; }
    public Printer getPrinter() { return printer; }
    public CashDispenser getCashDispenser() { return cashDispenser; }
    public IBankService getBankService() { return bankService; }
    public Card getCurrentCard() { return currentCard; }
    public IATMState getCurrentState() { return currentState; }

    //setters
    public void setCurrentCard(Card currentCard) { this.currentCard = currentCard; }
    public void setCurrentState(IATMState state) {
        this.currentState.onExit();
        this.currentState = state;
        this.currentState.onEnter();
    }

}
