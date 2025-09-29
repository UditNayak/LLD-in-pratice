# Low-Level Design (LLD) for ATM System

## States of an ATM
Let’s carefully enumerate the **ATM lifecycle** with states:

1. **Ready State**:
    - ATM is waiting for a card.
    - Action: `insertCard()` → **CardInsertedState**.
    - Any other call (PIN, transaction, etc.) → show error “Insert card first”.

2. **CardInserted State**:
    - Card is readed & Validated.
    - ATM will lock the card.
    - Screen asks for PIN.
    - Action: 
        - `enterPIN(correct)` → **AuthenticatedState**.
        - `enterPIN(incorrect)`(X attempts) → **EjectingState** (or release card).
        - `cancel()` → **EjectingState** (or unlock the card).
    - Any other call (transaction, etc.) → show error “Enter PIN first”.

3. **Authenticated State**:
    - User authenticated.
    - Screen shows menu (Withdraw, Mini Statement, Deposit, Change PIN, Cancel).
    - Action:
        - `selectTransaction(Withdraw)` → **TransactionProcessingState**.
        - `selectTransaction(MiniStatement)` → **TransactionProcessingState**.
        - `selectTransaction(Deposit)` → **TransactionProcessingState**.
        - `selectTransaction(ChangePIN)` → **TransactionProcessingState**.
        - `cancel()` → **EjectingState** (or release the card).
    - Any other call (PIN, etc.) → show error “Select transaction or cancel”.

4. **TransactionProcessing State**:
    - ATM executes selected transaction.
    - For withdrawal: checks inventory + bank balance.
    - Action for withdrawal:
        - Success → **DispensingState**.
        - Failure → **EjectingState** (with error display on screen).
        - `cancel()` → **EjectingState** (or release the card).
    - Any other call (PIN, etc.) → show error “Transaction in progress, please wait”.

5. **DispensingState**:
    - ATM dispenses cash physically.
    - Action → **PrintingState**.
    - If the user press `cancel()` during dispensing then no action is taken. Since the Transaction is already in progress.
    - Any other call (PIN, etc.) → show error “Dispensing cash, please wait”.

6. **PrintingState**:
    - Printer prints receipt (success or failure).
    - Action → **EjectingState**.
    - If the user press `cancel()` during printing then no action is taken. Since the Transaction is already completed.
    - Any other call (PIN, etc.) → show error “Printing in progress, please wait”.

7. **EjectingState**:
    - ATM ejects card, session ends.
    - Action → **ReadyState**.
    - Any other call (PIN, etc.) → show error “Ejecting card, please wait”.

### 1. Card Reader
```
class Card {
    - cardNumber: String
    - expiryDate: Date
}

class CardReader {
    + readCard(): Card
    + ejectCard(): Void
}
```

#### Why separate Card class with just cardNumber and expiryDate? Why not have these attributes in CardReader?
- Extensibility: If later you need CVV, chip data, cardholderName, etc., it’s already extendable.
- Card class is **immutable** → simplifies passing around between ATM states.
- It makes method signatures cleaner (`insertCard(Card)` vs `insertCard(String cardNumber, Date expiry, …)`).

### 2. Keypad Interface and Implementations
```
interface IKeyPad {
    + readPIN(): String
    + readAmount(): int
    + readInput(prompt: String): String
}

class PhysicalKeyPad implements IKeyPad { ... }

// In future
class TouchScreenKeyPad implements IKeyPad { ... }
class VoiceInputKeyPad implements IKeyPad { ... }
class MultiLanguageKeyPad implements IKeyPad { ... }
```

### 3. Screen
```
class Screen {
    + showMessage(msg: String): void
    + showOptions(options: List<String>): int   // returns chosen index
    + showError(err: String): void
}
```

### 4. Printer
```
class Printer {
    + printReceipt(transaction: Transaction): Void
}
```
- Printer just prints the receipt. It does not handle errors or messages; that’s the Screen’s job.
- In future, it will print mini statements, etc.

### 5. ICashDispenserStrategy Interface and Implementation
```
interface ICashDispenserStrategy {
    + compute(amount: Double, inventory: Map<Denomination, Int>): Map<Denomination, Int>
}

class LeastNumberOfNotesStrategy implements ICashDispenserStrategy {
    + compute(amount: Double, inventory: Map<Denomination, Int>): Map<Denomination, Int>
}

// In future, we can have other strategies like
class PreserveSmallDenomStrategy implements ICashDispenserStrategy { ... }
class EqualDistributionStrategy implements ICashDispenserStrategy { ... }
class MaximumSmallDenominationsStrategy implements ICashDispenserStrategy { ... }
class UserPreferenceStrategy implements ICashDispenserStrategy { ... }
class MinimumLargeDenominationsStrategy implements ICashDispenserStrategy { ... }
class RandomizedDispenserStrategy implements ICashDispenserStrategy { ... }
```

### 6. CashDispenser (Singleton, Strategy) and Denomination Enum
```
enum Denomination {
    TEN(10), TWENTY(20), FIFTY(50), HUNDRED(100), FIVE_HUNDRED(500), TWO_THOUSAND(2000)
}

class CashDispenser {
    - inventory: Map<Denomination, Int>
    - strategy: ICashDispenserStrategy
    - static instance: CashDispenser

    - CashDispenser()               // private constructor for singleton
    + getInstance(): CashDispenser
    + setStrategy(strategy: ICashDispenserStrategy): Void
    - totalAmount(): int


    + canDispenseAmount(amount: int): Boolean
    + computeDispense(amount: int): Map<Denomination, Int>
    + dispense(amount: int): Void
}
```

### 7. Bank Service and Proxy
```
interface IBankService {
    + validatePIN(card: Card, pin: String): Boolean
    + hasSufficientBalance(card: Card, amount: Double): Boolean
    + hasNotExceededLimit(card: Card, amount: Double): Boolean
    + debit(card: Card, amount: Double): Void
    
    // In future
    + getMiniStatement(card: Card): List<String>
    + changePIN(card: Card, oldPIN: String, newPIN: String): Boolean
    + credit(card: Card, amount: Double): Void
}

class BankService implements IBankService {
    + validatePIN(card: Card, pin: String): Boolean
    + hasSufficientBalance(card: Card, amount: Double): Boolean
    + hasNotExceededLimit(card: Card, amount: Double): Boolean
    + debit(card: Card, amount: Double): Void
}

class BankServiceProxy implements IBankService {
    - realService: IBankService
    + validatePIN(card: Card, pin: String): Boolean
    + hasSufficientBalance(card: Card, amount: Double): Boolean
    + hasNotExceededLimit(card: Card, amount: Double): Boolean
}

// Proxy handles retries, timeouts, logging, etc.
```

### 8. Transaction (Abstract Class), Concrete Classes, Enums and Factory
```
enum TransactionType { WITHDRAW, DEPOSIT, CHANGE_PIN, MINI_STATEMENT }

abstract class Transaction {
    // Need to Decide what attributes and methods we will have here
}

Concrete Classes:
class WithdrawTransaction extends Transaction { ... }

// In future
class DepositTransaction extends Transaction { ... }
class ChangePINTransaction extends Transaction { ... }
class MiniStatementTransaction extends Transaction { ... }


// Factory
class TransactionFactory {
    + createTransaction(type: TransactionType, card: Card, amount: Double): Transaction
}
```

### 9. ATM States Interface and Implementations
```
interface IATMState {
    // State lifecycle methods
    void onEnter();     // Called when entering the state
    void onExit();      // Called when exiting the state

    // Card operations
    void insertCard(Card card);
    void ejectCard();

    // User input operations
    void enterPIN(String pin);
    void selectTransaction(TransactionType type);
    void enterAmount(double amount);
    void cancel();

    // Internal system operations
    void processTransaction();
    void dispenseCash();
    void printReceipt();
}

class ReadyState implements IATMState { ... }
class CardInsertedState implements IATMState { ... }
class AuthenticatedState implements IATMState { ... }
class TransactionProcessingState implements IATMState { ... }
class DispensingState implements IATMState { ... }
class PrintingState implements IATMState { ... }
class EjectingState implements IATMState { ... }

// We will also have a State Factory to create states
class ATMStateFactory {
    + createState(stateType: Enum, atm: ATM): IATMState
}
```

### 10. ATM (Orchestrator)
```
class ATM {
    - id: String
    - location: String
    - state: IState
    - cardReader: CardReader
    - keyPad: IKeyPad
    - screen: Screen
    - cashDispenser: CashDispenser
    - printer: Printer
    - bankService: IBankService

    // Need to Decide What methods we will have here
}
```


## Design Patterns Used
1. **Immutable**: `Card`.
2. **Strategy**: `ICashDispenserStrategy` and its implementations.
3. **Singleton**: `CashDispenser`
4. **Proxy**: `BankServiceProxy`
5. **Factory**: `TransactionFactory`
6. **State**: `IState` and its implementations.

## Folder Structure
```
atm/
│── docs/
│   ├── Design-Requirements.md
│   └── LLD.md
│── src/
    /card
        Card.java               (Implemented)
        CardValidator.java     (Implemented)
    /hardware
        /cardreader
            ICardReader.java   (Implemented)
            SimpleCardReader.java (Implemented)
        /keypad
            IKeyPad.java      (Implemented)
            PhysicalKeyPad.java (Implemented)
            SimulatedKeyPad.java (Implemented)
        Screen.java          (Implemented)
        Printer.java         (To be implemented)
    /enums
        Denomination.java     (Implemented)
        TransactionType.java  (To be implemented)
    /strategy
        ICashDispenserStrategy.java (Implemented)
        LeastNumberOfNotesStrategy.java (Implemented)
    /bank
        IBankService.java     (Implemented)
        BankService.java      (Implemented)
        BankServiceProxy.java (Implemented)
    /transaction
        Transaction.java      (To be implemented)
        WithdrawTransaction.java (To be implemented)
        TransactionFactory.java (To be implemented)
    /state
        IATMState.java        (To be implemented)
        ReadyState.java       (To be implemented)
        CardInsertedState.java (To be implemented)
        AuthenticatedState.java (To be implemented)
        TransactionProcessingState.java (To be implemented)
        DispensingState.java  (To be implemented)
        PrintingState.java    (To be implemented)
        EjectingState.java    (To be implemented)
        ATMStateFactory.java  (To be implemented)
    /core
        CashDispenser.java    (Implemented)
        ATM.java              (To be implemented)
    
```