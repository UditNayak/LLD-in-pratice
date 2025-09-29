package state;

import core.ATM;

public class ATMStateFactory {
    public static IATMState getState(String stateName, ATM atm) {
        if (stateName == "ReadyState") {
            return new ReadyState(atm);
        }
        // Add other states here as needed
        throw new IllegalArgumentException("Unknown state: " + stateName);
    }
}
