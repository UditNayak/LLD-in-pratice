package service;

import core.ParkingLot;
import model.Ticket;
import model.VehicleType;

/**
 * GateKeeper is the main interface for customers.
 * Instead of directly talking to ParkingLot or Gates,
 * customer interacts through this "facade".
 */
public class GateKeeper {
    private final ParkingLot parkingLot;

    public GateKeeper(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    /**
     * Customer enters the parking lot.
     */
    public Ticket enter(String licensePlate, VehicleType vehicleType, boolean wantsPowerPlug, String entryGateId) {
        Ticket ticket = parkingLot.allocateSpot(licensePlate, vehicleType, wantsPowerPlug, entryGateId);
        if (ticket == null) {
            System.out.println("❌ Sorry, no spots available for vehicle: " + licensePlate);
            return null;
        }
        System.out.println("✅ Ticket issued: " + ticket.getTicketId() +
                           " | Spot: " + ticket.getAllocatedSpot().getId() +
                           " | Gate: " + entryGateId);
        return ticket;
    }

    /**
     * Customer exits the parking lot.
     */
    public void exit(String licensePlate, String exitGateId) {
        try {
            double bill = parkingLot.processExit(licensePlate, exitGateId);
            System.out.println("💳 Vehicle " + licensePlate + " exited at gate " + exitGateId +
                               ". Total bill: ₹" + bill);
        } catch (Exception e) {
            System.out.println("❌ Exit failed for " + licensePlate + ": " + e.getMessage());
        }
    }

    /**
     * Show active tickets (useful for debugging / operator view).
     */
    public void showActiveTickets() {
        System.out.println("📋 Active Tickets:");
        parkingLot.getAllActiveTickets().forEach(ticket -> {
            System.out.println(" - " + ticket.getLicensePlate() + " | Spot: " +
                               ticket.getAllocatedSpot().getId() + " | Gate: " +
                               ticket.getEntryGateId());
        });
    }
}
