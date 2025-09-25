# Object-Oriented Design - ATM

## Initial Requirements
- Has a card reader to read ATM cards (Extensible to all banks)
- Has an Interface for Entering PIN (Physical Keypad or Touch Screen)
- Has a Cash Dispenser to dispense cash
    - There can be some Cash Dispenser Strategy.
    - One simple strategy can be to dispense the least number of currency notes.
- Has a Printer (prints the details of the transaction)
    - If a transaction has taken place, it should print the details of the transaction.
    - If an error has occurred, it should print the error message.

## Note:
- There can be a limit on the total amount that can be withdrawn in a day. It can be a variable.

## Future Requirements:
- Mini statement (printable)
- Change PIN
- Deposit (There will be a cash deposit slot, different from cash dispenser)