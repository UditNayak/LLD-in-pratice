package hardware;

/**
 * Simple paper printer abstraction.
 */
public class Printer {
    public void printReceipt(String receiptText){
        System.out.println("[PRINTER] ---- RECEIPT ----");
        System.out.println(receiptText);
        System.out.println("[PRINTER] ----------------");
    }
}