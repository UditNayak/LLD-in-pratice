package strategy;

import java.util.Map;
import enums.Denomination;

public interface ICashDispenserStrategy {
    /**
     * Compute a map of denomination -> count to satisfy the amount using inventory.
     * Return null if impossible.
     */
    Map<Denomination, Integer> compute(int amount, Map<Denomination, Integer> inventory);
}
