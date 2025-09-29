package hardware.keypad;

public interface IKeyPad {
    String readPIN();
    int readAmount();
    String readInput(String prompt);
}
