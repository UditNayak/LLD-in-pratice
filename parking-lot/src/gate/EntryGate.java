package gate;

import core.ParkingLot;
import model.Ticket;
import model.VehicleType;

public class EntryGate {
    private final String id;
    private final ParkingLot parkingLot;

    public EntryGate(String id, ParkingLot parkingLot) {
        this.id = id;
        this.parkingLot = parkingLot;
    }

    public String getId() { return id; }

    /**
     * Issue ticket: forwards to parking lot and returns Ticket (or null if full)
     */
    public Ticket issueTicket(String licensePlate, VehicleType vehicleType, boolean wantsPowerPlug) {
        // call allocate on parkingLot with licensePlate
        Ticket ticket = parkingLot.allocateSpot(licensePlate, vehicleType, wantsPowerPlug, this.id);
        return ticket;
    }
}