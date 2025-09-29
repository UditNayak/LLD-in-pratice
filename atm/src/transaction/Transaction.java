package transaction;

import java.time.Instant;
import java.util.UUID;

import card.Card;
import enums.TransactionStatus;

/**
 * Base transaction abstraction
 */
public abstract class Transaction {
    protected final String transactionId = UUID.randomUUID().toString();
    protected final Card card;
    protected final int amount;
    protected final Instant timestamp = Instant.now();
    protected TransactionStatus status = TransactionStatus.PENDING;


    protected Transaction(Card card, int amount){
        this.card = card;
        this.amount = amount;
    }

    // getters
    public String getTransactionId(){ return transactionId; }
    public int getAmount(){ return amount; }
    public TransactionStatus getStatus(){ return status; }
    public Instant getTimestamp(){ return timestamp; }

    /**
     * Validate preconditions (ATM has cash, bank has balance, etc.)
     */
    public abstract boolean validate();

    /**
     * Execute the transaction. Return true on success.
     */
    public abstract boolean execute();

    /**
     * Rollback partial changes (if any).
     */
    public abstract void rollback();

    /**
     * Return a printable receipt text for this transaction.
     */
    public String buildReceiptText(){
        return "TransactionId: " + transactionId + "\nAmount: " + amount + "\nStatus: " + status + "\nTime: " + timestamp;
    }
}
