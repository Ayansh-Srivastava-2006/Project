import ui.AddMenuItemUI;

public class executeMenuEdit {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new AddMenuItemUI(() -> {
                // Runnable onSuccess callback
                System.out.println("Menu item added successfully");
            }).setVisible(true);
        });
    }
}