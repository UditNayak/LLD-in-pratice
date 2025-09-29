package hardware;

import java.util.List;
import java.util.Scanner;

public class Screen {
    private final Scanner scanner = new Scanner(System.in);

    public void showMessage(String msg){ System.out.println("[SCREEN] " + msg); }

    public int showOptions(List<String> options){
        // First Display all options
        // Then based on user input return the selected option index
        System.out.println("[SCREEN] Options:");
        for(int i=0;i<options.size();i++){
            System.out.println("[SCREEN] " + (i+1) + ". " + options.get(i));
        }
        
        System.out.print("Please select an option (1-" + options.size() + "): ");
        int choice = scanner.nextInt();
        return choice - 1; // return 0-based index
    }

    public void showError(String err){ System.out.println("[SCREEN][ERROR] " + err); }
}