import core.CashDispenser;
import enums.Denomination;

public class ATMApplication {
    public static void main(String[] args) {
        CashDispenser dispenser = CashDispenser.getInstance();
        dispenser.dispense(15010);
    }
}
