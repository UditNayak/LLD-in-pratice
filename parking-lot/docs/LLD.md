# Low-Level Design (Functonality-First Approach)
> We start by identifying **what the system should do** and then derive **what components/entities** are required to perform those actions.

## Example Workflow
1. **Entry**:
    - Customer arrives → EntryGate asks ParkingLot for available spot → Spot allocated → Ticket generated.
2. **Parking**:
    - Vehicle parked → Spot marked occupied.
3. **Exit**:
    - Customer reaches ExitGate → Ticket scanned → BillingCalculator computes fee → Payment processed → Spot freed.

## Core Functionalities

### Functionality 1: Entry Management
- **Customer arrives at EntryGate** with license plate, vehicle type, and optionally wants power plug.
- **Gate forwards request to ParkingLot** (via GateManager or directly).
- **ParkingLot checks availability** of suitable spots (based on size, power plug, fallback rules).
- **SpotAllocationStrategy** selects the best candidate spot (e.g., nearest to gate).
- **Ticket is generated** with all details: vehicle info, entry time, gate ID, allocated spot.
- **Ticket is stored** in ParkingLot's records.
- **Spot status updated** (OCCUPIED).
- **Ticket handed to customer** as proof of entry.

#### Parking Spot
```
enum class SpotStatus {
    AVAILABLE,
    OCCUPIED,
    OUT_OF_SERVICE  // (Under Maintenance)
}

enum class SpotSize {
    SMALL,   // e.g., Motorcycle
    MEDIUM,  // e.g., Car
    LARGE    // e.g., Truck
}

class Spot {
    - id: String // Unique identifier (e.g., "F1-R2-S3" for Floor 1, Row 2, Spot 3)
    - floor: Int
    - row: Int
    - number: Int
    - size: SpotSize
    - status: SpotStatus
    - hasPowerPlug: Boolean
    + isAvailable(): Boolean
    + occupy(): Void
    + free(): Void
}
```

#### Spot Allocation Strategy
```
interface SpotAllocationStrategy {
  // chooses one spot from candidates based on policy (e.g., nearest to entryGate)
  Spot selectSpot(List<Spot> candidates, String entryGateId)
}

Implementation: NearestSpotAllocationStrategy
```

#### Ticket
```
enum class VehicleType {
    MOTORCYCLE,
    CAR,
    BUS,
    TRUCK
}

class Ticket {
    - ticketId: String  // (e.g., UUID)
    - vehicleType: VehicleType
    - licensePlate: String
    - entryTime: DateTime
    - allocatedSpot: Spot
    - entryGateId: String
}
```

#### Parking Lot
```
class ParkingLot {
    - spots: List<Spot>
    - spotAllocationStrategy: SpotAllocationStrategy
    - entryGates: List<EntryGate>
    - ticketHistory: List<Ticket>  // all closed tickets
    - tickets: Map<String, Ticket>  // active tickets by licensePlate


    // Allocates spot and issues ticket, or returns null if full
    + allocateSpot(vehicleType, wantsPowerPlug, entryGateId): Ticket? 

    + findAvailableSpots(minSize: SpotSize, wantsPowerPlug: Boolean): List<Spot>   // for reporting / candidate list
}
```

#### Entry Gate
```
class EntryGate {
  id: String
  parkingLot: ParkingLot   // backpointer to single parent ParkingLot

  // called when human (GateManager) or UI submits entry
  issueTicket(licensePlate, vehicleType, wantsPowerPlug): Ticket?
}
```

### Functionality 2: Exit Management
- **Customer arrives at ExitGate** and presents ticket (via license plate or ticketId).
- **System retrieves the Ticket** from ParkingLot’s records.
- **Billing calculation** happens:
    - Duration calculated from entryTime to current time.
    - Base fee determined by spot size.
    - Additional fees for power plug if opted.
- **Payment processing** (abstract for now, can be extended later).
- **Spot freed** → status changes from OCCUPIED → AVAILABLE.
- **Ticket marked as closed** (so it can’t be reused).

#### Ticket (updated)
- We need to add a flag to identify if the vehicle has exited or not. 
- Otherwise, a customer can reuse the same ticket to exit multiple times.

```
class Ticket {
    ...
    - exitTime: DateTime? = null  // null if not exited yet
    - exitGateId: String? = null
    + markExited(exitTime: DateTime, exitGateId: String): Void
    ...
}
```

#### Pricing Strategy

```
interface PricingStrategy {
    fun calculatePrice(ticket: Ticket, exitTime: DateTime): Double
}

Implementation: PerHourPricingStrategy
```

#### Exit Gate
```
class ExitGate {
    - id: String
    - parkingLot: ParkingLot   // backpointer to parent ParkingLot

    // Processes exit: frees spot, updates ticket, calculates bill
    fun processExit(ticketId: String): Double
}
```

#### Parking Lot (updated)
```
class ParkingLot {
    ...
    - pricingStrategy: PricingStrategy
    - exitGates: List<ExitGate>
    ...
    + processExit(ticketId: String, exitGateId: String): Double
}
```

### Parking Lot Builder
- A utility class to create sample parking lot with predefined spots, gates, and strategies for testing
```
class ParkingLotBuilder {
    + buildSampleParkingLot(): ParkingLot
}
```

### UML
![UML Diagram](https://drive.google.com/uc?export=view&id=1jaFr-9QsqcnD1L_HDcG_WJG_hIZlcP44)