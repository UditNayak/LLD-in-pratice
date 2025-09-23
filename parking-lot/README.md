# Multilevel Parking Lot Design

## UML Diagram
![UML Diagram](https://drive.google.com/uc?export=view&id=1jaFr-9QsqcnD1L_HDcG_WJG_hIZlcP44)

## File Structure
```
parking-lot/
├── docs/
│   ├── Design-Requirements.md   # Original requirements & follow-ups
│   └── LLD.md                   # Low-Level Design (Functionality-First)
├── README.md
└── src/
    ├── Main.java                 # Entry point of the application
    ├── core/
    │   └── ParkingLot.java       # ParkingLot class (manages spots, tickets, gates)
    ├── builder/                  # Parking lot builder utilities
    │   └── ParkingLotBuilder.java
    ├── gate/                     # Entry and exit gates
    │   ├── EntryGate.java
    │   └── ExitGate.java
    ├── model/                    # Data models / entities
    │   ├── Spot.java
    │   ├── Ticket.java
    │   ├── SpotSize.java         # Enum for spot sizes
    │   ├── SpotStatus.java       # Enum for spot status
    │   └── VehicleType.java      # Enum for vehicle types
    ├── strategy/                 # Strategy interfaces and implementations
    │   ├── ISpotAllocationStrategy.java
    │   ├── IPricingStrategy.java
    │   ├── NearestSpotAllocationStrategy.java
    │   └── PerHourPricingStrategy.java
    └── exception/                # Custom exceptions
        └── TicketNotFoundException.java
```

## Output:
```shell
--- Parking Lot Simulation Start ---

✅ Ticket issued: TICKET-27f8ff25-7927-4a10-87ca-0e31e232d25f | Spot: F1-R1-S3 | Gate: EG1
✅ Ticket issued: TICKET-6ee0a614-8289-4843-8df0-67a0e3dab44d | Spot: F1-R1-S1 | Gate: EG2
📋 Active Tickets:
 - KA-01-AB-1234 | Spot: F1-R1-S3 | Gate: EG1
 - KA-02-CD-5678 | Spot: F1-R1-S1 | Gate: EG2
💳 Vehicle KA-01-AB-1234 exited at gate XG1. Total bill: ₹65.0
💳 Vehicle KA-02-CD-5678 exited at gate XG1. Total bill: ₹35.0
📋 Active Tickets:

--- Simulation End ---
```