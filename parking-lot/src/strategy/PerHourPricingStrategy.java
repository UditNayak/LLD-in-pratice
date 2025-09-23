package strategy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import model.SpotSize;
import model.Ticket;

public class PerHourPricingStrategy implements IPricingStrategy {
    private final Map<SpotSize, Double> baseSlotPrice = new HashMap<>();
    private final Map<SpotSize, Double> ratePerHour = new HashMap<>();
    private final double powerPlugRatePerHour = 5.0;

    public PerHourPricingStrategy() {
        baseSlotPrice.put(SpotSize.SMALL, 20.0);
        baseSlotPrice.put(SpotSize.MEDIUM, 40.0);
        baseSlotPrice.put(SpotSize.LARGE, 60.0);

        ratePerHour.put(SpotSize.SMALL, 10.0);
        ratePerHour.put(SpotSize.MEDIUM, 20.0);
        ratePerHour.put(SpotSize.LARGE, 30.0);
    }

    private double computePowerPlugCharge(Ticket ticket) {
        // This is based on abstruction. For now we assume power plug is used and return the duration of uses.
        // In reality, it will talk to the power plug system to get the actual usage duration.
        if (ticket.getAllocatedSpot().hasPowerPlug()) {
            LocalDateTime entry = ticket.getEntryTime();
            LocalDateTime exit = ticket.getExitTime() != null ? ticket.getExitTime() : LocalDateTime.now();
            long seconds = Duration.between(entry, exit).getSeconds();
            long hours = (seconds + 3600 - 1) / 3600; // round up to nearest hour
            if (hours <= 0) hours = 1;
            return hours * powerPlugRatePerHour;
        }
        return 0.0;
    }

    @Override
    public double calculatePrice(Ticket ticket, LocalDateTime exitTime) {
        LocalDateTime entry = ticket.getEntryTime();
        long seconds = Duration.between(entry, exitTime).getSeconds();
        // round up to nearest hour
        long hours = (seconds + 3600 - 1) / 3600;
        if (hours <= 0) hours = 1;

        double basePrice = baseSlotPrice.get(ticket.getAllocatedSpot().getSize());
        double hourlyRate = ratePerHour.get(ticket.getAllocatedSpot().getSize());
        double total = basePrice + (hourlyRate * hours);
        total += computePowerPlugCharge(ticket);

        return total;
    }
}
