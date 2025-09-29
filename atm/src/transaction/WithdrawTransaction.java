package transaction;

import bank.IBankService;
import card.Card;
import core.CashDispenser;
import enums.TransactionStatus;

public class WithdrawTransaction extends Transaction{
    private final IBankService bankService;
    private final CashDispenser dispenser = CashDispenser.getInstance();

    public WithdrawTransaction(Card card, int amount, IBankService bankService){
        super(card, amount);
        this.bankService = bankService;
    }
    @Override
    public boolean validate() {
        // Validate Whether ATM has cash
        if(!dispenser.canDispenseAmount(amount)){
            System.out.println("[TXN] ATM has insufficient cash");
            this.status = TransactionStatus.FAILED;
            return false;
        }

        // Validate Whether Bank has balance
        if (!bankService.hasSufficientBalance(card, amount)){
            System.out.println("[TXN] Bank has insufficient balance");
            this.status = TransactionStatus.FAILED;
            return false;
        }

        // Validate Whether Bank has not exceeded limit
        if (!bankService.hasNotExceededLimit(card, amount)){
            System.out.println("[TXN] Bank has exceeded withdrawal limit");
            this.status = TransactionStatus.FAILED;
            return false;
        }

        return true;
    }

    @Override
    public boolean execute() {
        if (!validate()){
            return false;
        }

        Boolean isDebited = bankService.debitAccount(card, amount);
        if (!isDebited){
            System.out.println("[TXN] Unable to debit account");
            this.status = TransactionStatus.FAILED;
            return false;
        }

        Boolean isDispensed = dispenser.dispense(amount);
        if (!isDispensed){
            System.out.println("[TXN] Unable to dispense cash");
            this.status = TransactionStatus.FAILED;
            return false;
        }

        this.status = TransactionStatus.SUCCESS;

        return true;
    }

    @Override
    public void rollback() {
        System.out.println("[TXN] Rolling back withdrawal transaction");
    }

    @Override
    public String buildReceiptText() {
        StringBuilder sb = new StringBuilder();
        sb.append("WITHDRAW RECEIPT\n");
        sb.append("TxnId: ").append(transactionId).append("\n");
        sb.append("Card: ").append(card.getCardNumber()).append("\n");
        sb.append("Amount: ").append(amount).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Time: ").append(timestamp).append("\n");
        return sb.toString();
    }
    
}
