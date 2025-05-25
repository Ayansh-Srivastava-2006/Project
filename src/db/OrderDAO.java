package db;

import order.Order;
import order.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAO {
    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    public int saveOrder(Order order) throws SQLException {
        if (order == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order cannot be null and must contain items");
        }

        String insertOrderSQL = "INSERT INTO orders (total) VALUES (?)";
        String insertItemSQL = "INSERT INTO order_item (order_id, menu_item_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert Order
                try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
                    orderStmt.setBigDecimal(1, BigDecimal.valueOf(order.getTotalAmount()));
                    orderStmt.executeUpdate();

                    try (ResultSet keys = orderStmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            int orderId = keys.getInt(1);
                            order.setOrderId(orderId);

                            // Insert Items
                            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSQL)) {
                                for (OrderItem item : order.getItems()) {
                                    if (item == null || item.getItem() == null) {
                                        throw new IllegalArgumentException("Invalid order item");
                                    }
                                    
                                    int itemId = getMenuItemIdByName(conn, item.getItem().getName());
                                    if (itemId == -1) {
                                        throw new SQLException("Menu item not found: " + item.getItem().getName());
                                    }
                                    
                                    itemStmt.setInt(1, orderId);
                                    itemStmt.setInt(2, itemId);
                                    itemStmt.setInt(3, item.getQuantity());
                                    itemStmt.executeUpdate();
                                }
                            }
                            conn.commit();
                            return orderId;
                        }
                    }
                }
                throw new SQLException("Failed to get generated order ID");
            } catch (Exception e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error saving order", e);
                throw e;
            }
        }
    }

    private int getMenuItemIdByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT id FROM menu_item WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            stmt.setString(1, name);
            return rs.next() ? rs.getInt("id") : -1;
        }
    }
}