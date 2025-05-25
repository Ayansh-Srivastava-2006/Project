package ui;

import db.MenuItemDAO;
import menu.MenuItem;
import order.Order;
import order.OrderItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RestaurantUI extends JFrame {

    private final Order currentOrder;
    private JPanel orderPanel;
    private JTextArea orderSummaryArea;

    private JScrollPane menuScrollPane;

    public RestaurantUI() {
        setTitle("Restrauraunt Ordering System - Restaurant Menu");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        currentOrder = new Order();

        add(createSidebar(), BorderLayout.WEST);
        add(createMenuPanel(), BorderLayout.CENTER);
        add(createOrderPanel(), BorderLayout.EAST);

        // Add menu bar with logout option
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                dispose();
                // Call login screen or exit as needed
                System.exit(0);
            }
        });
        menu.add(logoutItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        setVisible(true);
    }

    // Left Panel - Sidebar
    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(150, 0));
        panel.setLayout(new GridLayout(6, 1));
        panel.setBackground(new Color(0, 51, 102)); // Domino's blue

        String[] categories = {"All", "Appetizer", "MainCourse", "Drink"};

        for (String cat : categories) {
            JButton btn = new JButton(cat);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(0, 102, 204));
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.addActionListener(e -> loadMenuItems(cat.equals("All") ? null : cat));
            panel.add(btn);
        }
        return panel;
    }

    // Center Panel - Menu Items
    private JScrollPane createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 2, 10, 10));
        menuPanel.setBackground(Color.WHITE);

        loadMenuItems(null, menuPanel);

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        return scrollPane;
    }

    private void loadMenuItems(String type) {
        if (menuScrollPane != null) {
            getContentPane().remove(menuScrollPane);
        }
        menuScrollPane = createMenuPanelFiltered(type);
        getContentPane().add(menuScrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JScrollPane createMenuPanelFiltered(String type) {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 2, 10, 10));
        menuPanel.setBackground(Color.WHITE);

        loadMenuItems(type, menuPanel);
        return new JScrollPane(menuPanel);
    }

    private void loadMenuItems(String type, JPanel panel) {
        panel.removeAll();
        SwingWorker<List<MenuItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MenuItem> doInBackground() throws Exception {
                MenuItemDAO dao = new MenuItemDAO();
                return dao.getAllMenuItems();
            }

            @Override
            protected void done() {
                try {
                    List<MenuItem> items = get();
                    for (MenuItem item : items) {
                        if (type == null || item.getClass().getSimpleName().equalsIgnoreCase(type)) {
                            JPanel itemPanel = new JPanel();
                            itemPanel.setLayout(new BorderLayout());
                            itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                            JLabel nameLabel = new JLabel(item.getName());
                            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
                            itemPanel.add(nameLabel, BorderLayout.NORTH);

                            JLabel priceLabel = new JLabel("₹" + item.getPrice());
                            itemPanel.add(priceLabel, BorderLayout.CENTER);

                            JButton addButton = new JButton("Add to Order");
                            addButton.addActionListener(e -> {
                                currentOrder.addItem(new OrderItem(item, 1));
                                updateOrderSummary();
                            });
                            itemPanel.add(addButton, BorderLayout.SOUTH);

                            panel.add(itemPanel);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RestaurantUI.this,
                            "Error loading menu items: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Right Panel - Order Summary
    private JPanel createOrderPanel() {
        orderPanel = new JPanel(new BorderLayout());
        orderPanel.setPreferredSize(new Dimension(250, 0));
        orderPanel.setBackground(new Color(240, 240, 240));

        JLabel title = new JLabel("🛒 Your Order");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        orderPanel.add(title, BorderLayout.NORTH);

        orderSummaryArea = new JTextArea();
        orderSummaryArea.setEditable(false);
        orderPanel.add(new JScrollPane(orderSummaryArea), BorderLayout.CENTER);

        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.setBackground(new Color(0, 102, 204));
        placeOrderBtn.setForeground(Color.WHITE);
        placeOrderBtn.addActionListener(e -> {
            double total = currentOrder.getTotalAmount();
            JOptionPane.showMessageDialog(this, "Order placed! Total: ₹" + total);
        });

        orderPanel.add(placeOrderBtn, BorderLayout.SOUTH);
        return orderPanel;
    }

    private void updateOrderSummary() {
        StringBuilder sb = new StringBuilder();
        for (OrderItem item : currentOrder.getItems()) {
            sb.append(item.getItem().getName())
                    .append(" x").append(item.getQuantity())
                    .append(" - ₹").append(item.getItem().getPrice() * item.getQuantity())
                    .append("\n");
        }
        sb.append("\nTotal: ₹").append(currentOrder.getTotalAmount());
        orderSummaryArea.setText(sb.toString());
    }
}