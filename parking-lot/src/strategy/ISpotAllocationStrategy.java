package strategy;

import java.util.List;

import model.Spot;

public interface ISpotAllocationStrategy {
    /**
     * Choose one spot from candidates for the given entryGateId.
     * Return null if none chosen.
     */
    Spot selectSpot(List<Spot> candidates, String entryGateId);
}
