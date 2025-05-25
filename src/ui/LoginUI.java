package ui;

import db.UserDAO;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginUI extends JFrame {
    private static final String ROLE_ADMIN = "Admin";
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_PASSWORD_LENGTH = 50;
    private int loginAttempts = 0;
    private final JButton loginBtn = new JButton("Login");
    private final JButton registerBtn = new JButton("Register");
    private final UserDAO userDAO;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private static final Logger LOGGER = Logger.getLogger(LoginUI.class.getName());

    public LoginUI() {
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        usernameField.setDocument(new LengthRestrictedDocument(MAX_USERNAME_LENGTH));
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password field  
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        passwordField.setDocument(new LengthRestrictedDocument(MAX_PASSWORD_LENGTH));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Role dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Role:"), gbc);

        roleComboBox = new JComboBox<>(new String[]{"Admin", "Customer"});
        gbc.gridx = 1;
        mainPanel.add(roleComboBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginBtn.setMnemonic(KeyEvent.VK_L);
        registerBtn.setMnemonic(KeyEvent.VK_R);
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        pack();

        // Event handlers
        loginBtn.addActionListener(e -> performLogin());
        registerBtn.addActionListener(e -> openRegisterDialog());
        passwordField.addActionListener(e -> performLogin());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });

        setVisible(true);
    }

    private void openRegisterDialog() {
        JDialog registerDialog = new JDialog(this, "Register", true);
        registerDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField regUsername = new JTextField(15);
        JPasswordField regPassword = new JPasswordField(15);
        JPasswordField confirmPassword = new JPasswordField(15);
        JComboBox<String> regRole = new JComboBox<>(new String[]{"Admin", "Customer"});
        JButton submitBtn = new JButton("Submit");

        gbc.gridx = 0;
        gbc.gridy = 0;
        registerDialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(regUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        registerDialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(regPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        registerDialog.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(confirmPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        registerDialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(regRole, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        registerDialog.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            String username = regUsername.getText();
            String password = new String(regPassword.getPassword());
            String confirm = new String(confirmPassword.getPassword());
            String role = (String) regRole.getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                showError("All fields are required");
                return;
            }

            if (!password.equals(confirm)) {
                showError("Passwords do not match");
                return;
            }

            try {
                userDAO.registerUser(username, password, role);
                JOptionPane.showMessageDialog(registerDialog,
                        "Registration successful!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Registration failed", ex);
                showError("Registration failed: " + ex.getMessage());
            }
        });

        registerDialog.pack();
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setVisible(true);
    }

    private void performLogin() {
        loginBtn.setEnabled(false);
        try {
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();
            String selectedRole = (String) roleComboBox.getSelectedItem();

            if (!validateInput(username, password, selectedRole)) {
                handleFailedAttempt();
                return;
            }

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    try {
                        return userDAO.validateUser(username, String.valueOf(password));
                    } catch (Exception e) {
                        Logger.getLogger(LoginUI.class.getName()).log(Level.SEVERE,
                                "Login validation error", e);
                        return null;
                    } finally {
                        java.util.Arrays.fill(password, '0');
                    }
                }

                @Override
                protected void done() {
                    try {
                        String role = get();
                        if (role != null && role.equals(selectedRole)) {
                            openAppropriateUI(role);
                        } else {
                            handleFailedAttempt();
                        }
                    } catch (Exception e) {
                        showError("An error occurred. Please try again later.");
                        Logger.getLogger(LoginUI.class.getName()).log(Level.SEVERE,
                                "Login error", e);
                    } finally {
                        loginBtn.setEnabled(true);
                        passwordField.setText("");
                    }
                }
            };
            worker.execute();

        } catch (Exception e) {
            loginBtn.setEnabled(true);
            Logger.getLogger(LoginUI.class.getName()).log(Level.SEVERE,
                    "Unexpected login error", e);
            showError("An error occurred. Please try again later.");
        }
    }

    private boolean validateInput(String username, char[] password, String role) {
        if (username == null || username.isEmpty()) {
            showError("Username is required");
            return false;
        }
        if (password == null || password.length == 0) {
            showError("Password is required");
            return false;
        }
        if (role == null || role.isEmpty()) {
            showError("Role selection is required");
            return false;
        }
        return true;
    }

    private void handleFailedAttempt() {
        loginAttempts++;
        showError("Invalid login attempt");
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            showError("Too many failed attempts. Please try again later.");
            dispose();
        }
    }

    private void openappropriateUI(String role) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if ("Admin".equals(role)) {
                new AdminUI();
            } else {
                new RestaurantUI();
            }
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void cleanup() {
        try {
            if (userDAO instanceof AutoCloseable) {
                ((AutoCloseable) userDAO).close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void openAppropriateUI(String role) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if (ROLE_ADMIN.equals(role)) {
                new AdminUI();
            } else {
                new RestaurantUI();
            }
        });
    }
}

class LengthRestrictedDocument extends PlainDocument {
    private final int limit;

    LengthRestrictedDocument(int limit) {
        this.limit = limit;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        if (str == null) return;
        if ((getLength() + str.length()) <= limit) {
            super.insertString(offs, str, a);
        }
    }
}