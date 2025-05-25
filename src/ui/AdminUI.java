package ui;

import db.MenuItemDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminUI extends JFrame implements AutoCloseable {
    private static final int MAX_NAME_LENGTH = 50;
    private static final String PROCESSING_MESSAGE = "Processing...";

    private final MenuItemDAO menuDAO;
    private final JTextField nameField;
    private final JTextField priceField;
    private final JComboBox<String> typeComboBox;
    private final JTextArea outputArea;

    public AdminUI() {
        menuDAO = new MenuItemDAO();
        nameField = new JTextField();
        priceField = new JTextField();
        typeComboBox = new JComboBox<>(new String[]{"APPETIZER", "MAIN_COURSE", "DRINK"});
        outputArea = new JTextArea();

        initializeUI();
        setupWindowListener();
    }

    private void initializeUI() {
        setTitle("Admin Panel");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add logout button at the top
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(logoutButton, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Price:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(typeComboBox, gbc);

        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(this::handleAddItem);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(addButton, gbc);

        outputArea.setEditable(false);
        outputArea.setRows(10);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        add(mainPanel);
        pack();
        setVisible(true);
    }

    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            close();
            // You can add additional logout logic here if needed
        }
    }

    private void handleAddItem(ActionEvent e) {
        String name = nameField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();

        if (!validateInput(name)) {
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    double price = Double.parseDouble(priceField.getText());
                    if (price <= 0) {
                        SwingUtilities.invokeLater(() ->
                                showError("Price must be greater than 0!"));
                        return null;
                    }

                    menuDAO.insertMenuItem(name, MenuItemDAO.MenuItemType.valueOf(type), price);
                    SwingUtilities.invokeLater(() -> {
                        showSuccess("Item added: " + name);
                        try {
                            StringBuilder items = new StringBuilder();
                            menuDAO.getAllMenuItems().forEach(item ->
                                    items.append(item.toString()).append("\n"));
                            outputArea.setText(items.toString());
                        } catch (SQLException ex) {
                            showError("Failed to refresh menu items.");
                        }
                        clearFields();
                    });
                } catch (NumberFormatException ex) {
                    SwingUtilities.invokeLater(() ->
                            showError("Invalid price format!"));
                } catch (SQLException ex) {
                    SwingUtilities.invokeLater(() ->
                            showError("Database error occurred. Please try again."));
                }
                return null;
            }
        };
        outputArea.setText(PROCESSING_MESSAGE);
        worker.execute();
    }

    private boolean validateInput(String name) {
        if (name.isEmpty()) {
            showError("Name cannot be empty!");
            return false;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            showError("Name is too long (max " + MAX_NAME_LENGTH + " characters)");
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        try {
            if (menuDAO instanceof AutoCloseable) {
                ((AutoCloseable) menuDAO).close();
            }
        } catch (Exception e) {
            // Log the error but don't show to user as we're closing anyway
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "Error closing MenuItemDAO", e);
        }
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        typeComboBox.setSelectedIndex(0);
    }
}