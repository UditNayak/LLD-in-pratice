package model;

import java.time.LocalDateTime;

import util.IdGenerator;

public class Ticket {
    private final String ticketId;
    private final VehicleType vehicleType;
    private final String licensePlate;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime; // null if not exited
    private final Spot allocatedSpot;
    private final String entryGateId;
    private String exitGateId; // set on exit
    private final boolean wantsPowerPlug;

    private String generateTicketId() {
        return "TICKET-" + IdGenerator.generate();
    }

    public Ticket(VehicleType vehicleType, String licensePlate,
                  LocalDateTime entryTime, Spot allocatedSpot, String entryGateId, boolean wantsPowerPlug) {
        this.ticketId = generateTicketId();
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.allocatedSpot = allocatedSpot;
        this.entryGateId = entryGateId;
        this.wantsPowerPlug = wantsPowerPlug;
    }

    public String getTicketId() { return ticketId; }
    public VehicleType getVehicleType() { return vehicleType; }
    public String getLicensePlate() { return licensePlate; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public Spot getAllocatedSpot() { return allocatedSpot; }
    public String getEntryGateId() { return entryGateId; }
    public String getExitGateId() { return exitGateId; }
    public boolean wantsPowerPlug() { return wantsPowerPlug; }

    public synchronized void markExited(LocalDateTime exitTime, String exitGateId) {
        if (this.exitTime != null) {
            throw new IllegalStateException("Ticket already closed: " + ticketId);
        }
        this.exitTime = exitTime;
        this.exitGateId = exitGateId;
    }
}
