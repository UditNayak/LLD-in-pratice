package transaction;

import bank.IBankService;
import card.Card;
import enums.TransactionType;

public class TransactionFactory {
    public static Transaction createTransaction(TransactionType type, Card card, int amount, IBankService bankService){
        switch(type){
            case WITHDRAW:
                return new WithdrawTransaction(card, amount, bankService);
            // future: DEPOSIT, CHANGE_PIN, MINI_STATEMENT
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }
    }
}