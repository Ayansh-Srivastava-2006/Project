package ui;

import db.MenuItemDAO;
import db.MenuItemDAO.MenuItemType;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AddMenuItemUI extends JFrame {

    public AddMenuItemUI(Runnable onSuccess) {
        setTitle("Add Menu Item");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField(20);

        JLabel typeLabel = new JLabel("Item Type:");
        String[] types = {"Appetizer", "MainCourse", "Drink"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(10);

        JButton submitBtn = new JButton("Add Item");

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(typeLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(typeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(priceLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(priceField, gbc);

        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 3;
        add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            String name = nameField.getText();
            String typeStr = (String) typeBox.getSelectedItem();
            MenuItemType type = MenuItemType.valueOf(typeStr.toUpperCase());
            String priceText = priceField.getText();

            if (name.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                new MenuItemDAO().insertMenuItem(name, type, price);
                JOptionPane.showMessageDialog(this, "Item added successfully!");
                onSuccess.run(); // Notify parent to reload menu
                dispose(); // Close window
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });
    }
}