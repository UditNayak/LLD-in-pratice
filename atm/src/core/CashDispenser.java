package core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import enums.Denomination;
import strategy.ICashDispenserStrategy;

/*
 * Singleton CashDispenser with strategy pattern for dispensing cash.
 * - private constructor with reflection guard
 * - getInstance() with DCL
 * - volatile keyword for thread visibility
 */

public class CashDispenser {
    private final Map<Denomination,Integer> inventory = new HashMap<>();
    private ICashDispenserStrategy strategy;
    private static volatile CashDispenser instance;

    private CashDispenser() {
        if (instance != null) {
            throw new RuntimeException("Reflection is not allowed to create singleton instance");
        }
        // initialize with some default notes for testing
        for (Denomination d : Denomination.values()) {
            inventory.put(d, 10); // 10 notes of each denomination
        }
        this.strategy = new strategy.LeastNumberOfNotesStrategy(); // default strategy
    }

    public static CashDispenser getInstance() {
        if (instance == null) {
            synchronized (CashDispenser.class) {
                if (instance == null) {
                    instance = new CashDispenser();
                }
            }
        }
        return instance;
    }

    public synchronized void setStrategy(ICashDispenserStrategy strategy){
        this.strategy = strategy;
    }

    public synchronized int totalAmount(){
        int total = 0;
        for(Denomination d : inventory.keySet()){
            total += d.getValue() * inventory.get(d);
        }
        return total;
    }

    public synchronized boolean canDispenseAmount(int amount){
        if (amount <= 0 || amount > totalAmount()) return false;

        // Check if the denominations can sum up to the amount
        Map<Denomination,Integer> temp = computeDispense(amount);
        return temp != null;
    }

    public synchronized Map<Denomination,Integer> computeDispense(int amount){
        if(strategy == null) throw new IllegalStateException("Strategy not set");
        return strategy.compute(amount, Collections.unmodifiableMap(inventory));
    }

    public synchronized boolean dispense(int amount){
        // In a real ATM, this would interact with hardware to dispense cash
        Map<Denomination,Integer> toDispense = computeDispense(amount);
        if(toDispense == null) return false; // cannot dispense exact amount

        // Update inventory
        for(Denomination d : toDispense.keySet()){
            int currentCount = inventory.getOrDefault(d, 0);
            int dispenseCount = toDispense.get(d);
            if(dispenseCount > currentCount){
                throw new IllegalStateException("Insufficient notes during dispense");
            }
            inventory.put(d, currentCount - dispenseCount);
        }
        System.out.println("[CASH DISPENSER] Dispensing: " + toDispense);
        System.out.println("[CASH DISPENSER] Please collect your cash.");
        return true;
    }
}
