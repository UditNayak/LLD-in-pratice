package gate;

import core.ParkingLot;

public class ExitGate {
    private final String id;
    private final ParkingLot parkingLot;

    public ExitGate(String id, ParkingLot parkingLot) {
        this.id = id;
        this.parkingLot = parkingLot;
    }

    public String getId() { return id; }

    /**
     * Process exit by ticketId. Returns bill amount (throws if ticket invalid).
     */
    public double processExit(String ticketId) {
        return parkingLot.processExit(ticketId, this.id);
    }
}
