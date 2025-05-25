package db;

import menu.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MenuItemDAO {
    private static final Logger LOGGER = Logger.getLogger(MenuItemDAO.class.getName());

    public enum MenuItemType {
        APPETIZER("Appetizer"),
        MAIN_COURSE("MainCourse"),
        DRINK("Drink");

        private final String value;

        MenuItemType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Inserts a new menu item into the database.
     *
     * @param name  The name of the menu item (non-null, non-empty)
     * @param type  The type of the menu item
     * @param price The price of the menu item (must be positive)
     * @throws IllegalArgumentException if input parameters are invalid
     * @throws SQLException             if database operation fails
     */
    public void insertMenuItem(String name, MenuItemType type, double price) throws SQLException {
        validateInput(name, type, price);

        String sql = "INSERT INTO menu_item (name, type, price) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, type.getValue());
            stmt.setDouble(3, price);
            stmt.executeUpdate();
        }
    }

    public boolean deleteMenuItemByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        String sql = "DELETE FROM menu_item WHERE name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves all menu items from the database.
     *
     * @return List of MenuItem objects
     * @throws SQLException if database operation fails
     */
    public List<MenuItem> getAllMenuItems() throws SQLException {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_item";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                double price = rs.getDouble("price");

                MenuItem item = null;
                if (type.equals("Appetizer")) {
                    item = new Appetizer(name, price);
                } else if (type.equals("MainCourse")) {
                    item = new MainCourse(name, price);
                } else if (type.equals("Drink")) {
                    item = new Drink(name, price);
                }

                if (item != null) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    private void validateInput(String name, MenuItemType type, double price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }
}