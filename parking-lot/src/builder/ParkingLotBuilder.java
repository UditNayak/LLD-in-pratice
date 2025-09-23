package builder;
import core.ParkingLot;
import gate.EntryGate;
import gate.ExitGate;
import model.Spot;
import model.SpotSize;
import strategy.NearestSpotAllocationStrategy;
import strategy.PerHourPricingStrategy;

public class ParkingLotBuilder {
    public static ParkingLot buildSampleParkingLot() {
        ParkingLot lot = new ParkingLot("PL-1");

        // strategies
        lot.setSpotAllocationStrategy(new NearestSpotAllocationStrategy());
        lot.setPricingStrategy(new PerHourPricingStrategy());

        // create entry gates
        EntryGate eg1 = new EntryGate("EG1", lot);
        EntryGate eg2 = new EntryGate("EG2", lot);
        lot.addEntryGate(eg1);
        lot.addEntryGate(eg2);

        // create exit gates
        ExitGate xg1 = new ExitGate("XG1", lot);
        lot.addExitGate(xg1);

        // seed spots: Floor 1, row 1..2, spots 1..5
        int floor = 1;
        for (int row = 1; row <= 2; row++) {
            for (int num = 1; num <= 5; num++) {
                SpotSize size = (num <= 2) ? SpotSize.SMALL : (num <= 4 ? SpotSize.MEDIUM : SpotSize.LARGE);
                boolean plug = (num == 1) || (num == 3); // some small/medium spots have power plugs
                Spot s = new Spot(floor, row, num, size, plug);
                lot.addSpot(s);
            }
        }

        return lot;
    }
}