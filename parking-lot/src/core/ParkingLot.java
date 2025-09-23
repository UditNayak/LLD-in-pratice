package core;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import exception.TicketNotFoundException;
import gate.EntryGate;
import gate.ExitGate;
import model.Spot;
import model.SpotSize;
import model.Ticket;
import model.VehicleType;
import strategy.IPricingStrategy;
import strategy.ISpotAllocationStrategy;

/**
 * Central manager: stores spots, tickets, gates; performs allocation + exits
 */
public class ParkingLot {
    private final String id;
    private final List<Spot> spots = new ArrayList<Spot>();
    private final List<Ticket> ticketHistory = new ArrayList<>();
    private final Map<String, Ticket> tickets = new HashMap<>(); // active tickets by vehicle/license
    private final Map<String, EntryGate> entryGates = new HashMap<>();
    private final Map<String, ExitGate> exitGates = new HashMap<>();
    private ISpotAllocationStrategy spotAllocationStrategy;
    private IPricingStrategy pricingStrategy;

    public ParkingLot(String id) {
        this.id = id;
    }

    // configuration
    public void setSpotAllocationStrategy(ISpotAllocationStrategy s) { this.spotAllocationStrategy = s; }
    public void setPricingStrategy(IPricingStrategy p) { this.pricingStrategy = p; }

    // CRUD for spots/gates (for builder)
    public void addSpot(Spot spot) { spots.add(spot); }
    public void addEntryGate(EntryGate gate) {
        entryGates.put(gate.getId(), gate);
    }
    public void addExitGate(ExitGate gate) {
        exitGates.put(gate.getId(), gate);
    }

    public EntryGate getEntryGateById(String id) { return entryGates.get(id); }
    public ExitGate getExitGateById(String id) { return exitGates.get(id); }

    public List<Spot> getAllSpots() { return Collections.unmodifiableList(spots); }
    public Collection<Ticket> getAllActiveTickets() { return Collections.unmodifiableCollection(tickets.values()); }


    // Overload with licensePlate (used in EntryGate)
    public synchronized Ticket allocateSpot(String licensePlate, VehicleType vehicleType, boolean wantsPowerPlug, String entryGateId) {
        // Determine minimum spot size based on vehicle type
        SpotSize minSize = mapVehicleToSpotSize(vehicleType);

        // find candidate spots (size >= minSize, AVAILABLE)
        List<Spot> candidates = findAvailableSpots(minSize, wantsPowerPlug);

        if (candidates.isEmpty()) {
            // try fallback: larger sizes
            candidates = findAvailableSpots(nextLargerSize(minSize), wantsPowerPlug);
        }

        if (candidates.isEmpty()) {
            return null; // no spot
        }

        // Use strategy to pick one among candidates
        Spot chosen = spotAllocationStrategy.selectSpot(candidates, entryGateId);
        if (chosen == null) return null;

        // occupy the spot (we decided to mark OCCUPIED at ticket creation)
        chosen.occupy();

        Ticket ticket = new Ticket( vehicleType, licensePlate, LocalDateTime.now(), chosen, entryGateId, wantsPowerPlug);
        tickets.put(licensePlate, ticket);
        return ticket;
    }

    /**
     * Finds available spots with size >= minSize. If wantsPowerPlug true, prefer spots (both with and without plugs) — filtering left to caller.
     * For reporting / candidate list
     */
    public synchronized List<Spot> findAvailableSpots(SpotSize minSize, boolean wantsPowerPlug) {
        return spots.stream()
                .filter(s -> s.isAvailable())
                .filter(s -> isSizeFits(s.getSize(), minSize))
                .filter(s -> {
                    if (wantsPowerPlug) {
                        // include both with and without plug (we will prefer non-plug unless requested?), but per earlier rules: if wants plug, we try plug first.
                        return true;
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    private static boolean isSizeFits(SpotSize spotSize, SpotSize minSize) {
        // SMALL < MEDIUM < LARGE
        if (minSize == SpotSize.SMALL) return true;
        if (minSize == SpotSize.MEDIUM) return spotSize == SpotSize.MEDIUM || spotSize == SpotSize.LARGE;
        if (minSize == SpotSize.LARGE) return spotSize == SpotSize.LARGE;
        return false;
    }

    private static SpotSize nextLargerSize(SpotSize s) {
        if (s == SpotSize.SMALL) return SpotSize.MEDIUM;
        if (s == SpotSize.MEDIUM) return SpotSize.LARGE;
        return s;
    }

    private static SpotSize mapVehicleToSpotSize(VehicleType vt) {
        switch (vt) {
            case MOTORCYCLE: return SpotSize.SMALL;
            case CAR: return SpotSize.MEDIUM;
            case BUS:
            case TRUCK: return SpotSize.LARGE;
            default: return SpotSize.MEDIUM;
        }
    }

    /**
     * Process exit: marks ticket exited, frees spot, returns bill amount.
     * Throws if ticket not found or already closed.
     */
    public synchronized double processExit(String licensePlate, String exitGateId) {
        Ticket ticket = tickets.get(licensePlate);
        if (ticket == null) {
            throw new TicketNotFoundException("Ticket not found: " + licensePlate);
        }
        if (ticket.getExitTime() != null) {
            throw new IllegalStateException("Ticket already closed: " + licensePlate);
        }
        LocalDateTime exitTime = LocalDateTime.now();
        ticket.markExited(exitTime, exitGateId);
        ticketHistory.add(ticket);
        tickets.remove(licensePlate);

        // compute bill
        double bill = pricingStrategy.calculatePrice(ticket, exitTime);

        // free spot
        ticket.getAllocatedSpot().free();

        return bill;
    }
}
