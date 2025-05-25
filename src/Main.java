import javax.swing.SwingUtilities;

import ui.RestaurantUI;
import ui.LoginUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                LoginUI log = new LoginUI();
            } catch (Exception e) {
                System.err.println("Failed to start application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}