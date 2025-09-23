package strategy;

import java.util.Comparator;
import java.util.List;

import model.Spot;

/**
 * Simple nearest strategy: for demo pick the first available candidate.
 * In a real system, you'd have distances per gate. Here we pick smallest row/number ordering.
 */
public class NearestSpotAllocationStrategy implements ISpotAllocationStrategy {
    @Override
    public Spot selectSpot(List<Spot> candidates, String entryGateId) {
        if (candidates == null || candidates.isEmpty()) return null;
        // simple deterministic pick: smallest floor -> row -> number
        return candidates.stream()
                .min(Comparator.comparing(Spot::getId))
                .orElse(null);
    }
}
