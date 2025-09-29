package strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import enums.Denomination;

/**
 * Greedy approach: try largest denominations first. Works for typical currency systems.
 */
public class LeastNumberOfNotesStrategy implements ICashDispenserStrategy {

    @Override
    public Map<Denomination, Integer> compute(int amount, Map<Denomination, Integer> inventory) {
        if (amount <= 0) return Collections.emptyMap();

        List<Denomination> desc = new ArrayList<>(Arrays.asList(Denomination.values()));
        desc.sort((a,b)->Integer.compare(b.getValue(), a.getValue()));

        Map<Denomination, Integer> result = new LinkedHashMap<>();
        int remaining = amount;

        for (Denomination d : desc) {
            int denom = d.getValue();
            int available = inventory.getOrDefault(d, 0);
            int use = Math.min(remaining / denom, available);
            if (use > 0) {
                result.put(d, use);
                remaining -= use * denom;
            }
        }

        if (remaining != 0) return null; // cannot dispense exact amount
        return result;
    }
}
