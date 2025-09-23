package model;

public class Spot {
    private final String id; // e.g., F1-R2-S3
    private final int floor;
    private final int row;
    private final int number;
    private final SpotSize size;
    private SpotStatus status;
    private final boolean hasPowerPlug;

    private String generateId(int floor, int row, int number) {
        return "F" + floor + "-R" + row + "-S" + number;
    }

    public Spot(int floor, int row, int number, SpotSize size, boolean hasPowerPlug) {
        this.id = generateId(floor, row, number);
        this.floor = floor;
        this.row = row;
        this.number = number;
        this.size = size;
        this.hasPowerPlug = hasPowerPlug;
        this.status = SpotStatus.AVAILABLE;
    }

    public String getId() { return id; }
    public SpotSize getSize() { return size; }
    public SpotStatus getStatus() { return status; }
    public boolean hasPowerPlug() { return hasPowerPlug; }

    public synchronized boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }

    public synchronized void occupy() {
        if (status != SpotStatus.AVAILABLE) {
            throw new IllegalStateException("Spot not available to occupy: " + id);
        }
        status = SpotStatus.OCCUPIED;
    }

    public synchronized void free() {
        if (status == SpotStatus.OUT_OF_SERVICE) {
            // do not change if out of service
            return;
        }
        if (status != SpotStatus.OCCUPIED) {
            throw new IllegalStateException("Spot not occupied to free: " + id);
        }
        status = SpotStatus.AVAILABLE;
    }
}
