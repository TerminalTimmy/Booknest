// DatabaseConnection.java - Manages MySQL database connection (Demonstrates: JDBC)
// Uses JDBC to connect to the booknest_db MySQL database.
// Connection details are configured here.

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection provides a centralized utility for obtaining JDBC connections
 * to the MySQL database backing the BookNest system.
 *
 * Concepts Demonstrated:
 * 1. JDBC (Java Database Connectivity): Standard API for connecting to relational databases.
 * 2. STATIC UTILITY METHODS: Methods getConnection() and testConnection() can be invoked directly
 *    without instantiating the class (e.g., DatabaseConnection.getConnection()).
 * 3. EXCEPTION HANDLING: Catches SQLException to provide user-friendly error messages before rethrowing.
 */
public class DatabaseConnection {

    // Database configuration constants
    private static final String URL = "jdbc:mysql://localhost:3306/booknest_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Tu@110106"; // Change this to your MySQL root password if set

    // Static block to explicitly load MySQL JDBC driver class
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // In modern JDBC 4.0+, DriverManager auto-loads drivers from classpath,
            // but this ensures compatibility across all environments.
        }
    }

    /**
     * Obtains a new Connection to the BookNest database.
     *
     * @return Connection object to booknest_db
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to the database.");
            System.out.println("Please check that MySQL is running and the database 'booknest_db' exists.");
            System.out.println("Details: " + e.getMessage());
            throw e; // Re-throw so the calling code can handle it
        }
    }

    /**
     * Tests the database connection and prints the status.
     * Demonstrates try-with-resources for automatic closing of the Connection.
     *
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
