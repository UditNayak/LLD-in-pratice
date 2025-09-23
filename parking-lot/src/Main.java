import builder.ParkingLotBuilder;
import core.ParkingLot;
import model.Ticket;
import model.VehicleType;
import service.GateKeeper;

public class Main {
    public static void main(String[] args) {
        // 1. Build parking lot
        ParkingLot lot = ParkingLotBuilder.buildSampleParkingLot();

        // 2. Create GateKeeper
        GateKeeper gateKeeper = new GateKeeper(lot);

        // 3. Simulate customers
        System.out.println("\n--- Parking Lot Simulation Start ---\n");

        // Vehicle 1 enters
        Ticket t1 = gateKeeper.enter("KA-01-AB-1234", VehicleType.CAR, true, "EG1");

        // Vehicle 2 enters
        Ticket t2 = gateKeeper.enter("KA-02-CD-5678", VehicleType.MOTORCYCLE, false, "EG2");

        // Show active tickets
        gateKeeper.showActiveTickets();

        // Vehicle 1 exits
        if (t1 != null) {
            gateKeeper.exit(t1.getLicensePlate(), "XG1");
        }

        // Vehicle 2 exits
        if (t2 != null) {
            gateKeeper.exit(t2.getLicensePlate(), "XG1");
        }

        // Final state
        gateKeeper.showActiveTickets();

        System.out.println("\n--- Simulation End ---");
    }
}
