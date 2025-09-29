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

## Clarifications:
1. The Printer is printing the details of the transaction and also the error messages. Are we talking about the display screen here?
    - Yes, technically it can be a display screen.
    - For this project, we have 2 things - a display screen and a printer.
    - The display screen shows the options, messages, errors, etc.
    - The printer prints the details of the transaction (if a transaction has taken place) on a paper slip.

## User Journey for Cash Withdrawal:
1. User inserts the ATM card.
2. System reads the card and validates it.
    - User might enter an invalid card.
    - The card might be expired.
    - User might enter a non-supported bank's card (e.g. a card from a different country).
    - The card get stuck in the ATM Card Reader till the transaction is complete.
3. System presents the main transaction options (Withdraw Cash, View Mini Statement, Change PIN, Deposit Cash).
4. User selects the option to withdraw cash.
5. System prompts the user to enter the PIN.
6. User enters the PIN.
7. System validates the PIN with the bank.
    - Edge Case: User enters an incorrect PIN → System prompts to re-enter. (After X failed attempts, the card is retained/ejected, or account is locked.)
    - How the ATM communicates with the bank to validate the PIN.
        - The atm talks to it's own bank Service which in turn talks to the user's bank service.
        - For example, an HDFC ATM can be used by an ICICI bank customer. The HDFC ATM will talk to the HDFC bank service(Or may be RBI) which in turn will talk to the ICICI bank service to validate the PIN.
8. System prompts the user to enter the amount to withdraw.
9. User enters the amount.
10. System performs a series of pre-dispensing checks:
    - ATM Check: Verify if the requested amount is available in the ATM.
        - Edge Case: The denominations of currency notes are not available to dispense the requested amount (e.g., requesting $30 when only $20 and $50 notes are available).
        - Edge Case: The total amount available in the ATM is less than the requested amount.
    - Bank Check 1: Verify if the user has sufficient balance in their bank account.
        - Edge Case: User does not have sufficient balance.
    - Bank Check 2: Verify if the user has exceeded their daily withdrawal limit.
        - Edge Case: User has exceeded the daily withdrawal limit.
11. If all checks pass, the transaction is processed and the system dispenses the cash. (According to the cash dispenser strategy.)
12. System prints the transaction receipt (success details).
13. System ejects the card.
14. User takes the cash, the printed receipt, and the card.
15. System returns to the initial state.