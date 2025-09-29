package hardware.keypad;

import java.util.Scanner;

public class PhysicalKeyPad implements IKeyPad {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String readPIN() {
        System.out.print("Enter PIN: ");
        return scanner.nextLine().trim();
    }

    @Override
    public int readAmount() {
        System.out.print("Enter Amount: ");
        String s = scanner.nextLine().trim();
        try { return Integer.parseInt(s); } catch(Exception e){ return 0; }
    }

    @Override
    public String readInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
}