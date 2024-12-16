package Util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnection {
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/abc_cinema";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345678";

    // Static method to get a database connection
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace(); // Log exception details for debugging
        }
        return connection;
    }
}
