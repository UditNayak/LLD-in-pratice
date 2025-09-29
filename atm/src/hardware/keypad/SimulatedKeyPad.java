package hardware.keypad;

public class SimulatedKeyPad implements IKeyPad {
    @Override
    public String readPIN() {
        // Simulate reading a PIN from the simulated keypad
        String pin = "1234";
        System.out.println("Taken PIN input from SimulatedKeyPad = " + pin);
        return pin;
    }

    @Override
    public int readAmount() {
        // Simulate reading an amount from the simulated keypad
        int amount = 100;
        System.out.println("Taken Amount input from SimulatedKeyPad = " + amount);
        return amount;
    }

    @Override
    public String readInput(String prompt) {
        // Simulate reading generic input from the simulated keypad
        String input = "testInput";
        System.out.println(prompt + " (simulated) = " + input);
        return input;
    }
    
}
