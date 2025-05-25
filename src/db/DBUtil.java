package db;

import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3308/restaurant_app";
    private static final String USER = "root";
    private static final String PASS = "";  // Replace with your password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
