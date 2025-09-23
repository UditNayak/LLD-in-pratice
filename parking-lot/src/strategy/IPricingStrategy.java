package strategy;

import java.time.LocalDateTime;

import model.Ticket;

public interface IPricingStrategy {
    double calculatePrice(Ticket ticket, LocalDateTime exitTime);
}
