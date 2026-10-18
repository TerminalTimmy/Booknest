// AuthService.java - Handles user authentication (Demonstrates: JDBC, String handling, Exception handling)
// Provides login functionality and password hashing using SHA-256.
// Passwords are NEVER stored in plain text - we store SHA-256 hashes.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {

    /**
     * Hash a password using SHA-256.
     * This converts a plain text password into a fixed-length hex string.
     * Even a small change in the password produces a completely different hash.
     * We store this hash in the database instead of the actual password.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available in Java, this should never happen
            System.out.println("Error: Hashing algorithm not found.");
            return null;
        }
    }

    /**
     * Authenticate a user by ID and password.
     * Returns a User object (Student or Librarian) if credentials are valid.
     * Returns null if login fails.
     * 
     * Demonstrates:
     * - JDBC with PreparedStatement (prevents SQL injection)
     * - try-with-resources (automatic resource cleanup)
     * - Polymorphism (returns User reference that could be Student or Librarian)
     * - String handling (trim(), password comparison)
     */
    public static User login(int userId, String password) {
        // String handling: trim() removes leading/trailing whitespace
        String hashedPassword = hashPassword(password.trim());

        String sql = "SELECT user_id, name, password_hash, role FROM users WHERE user_id = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String hash = rs.getString("password_hash");
                    String role = rs.getString("role");

                    // Polymorphism: Creating the appropriate subclass based on role
                    if (role.equalsIgnoreCase("LIBRARIAN")) {
                        return new Librarian(id, name, hash);
                    } else {
                        return new Student(id, name, hash);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }

        return null;  // Login failed
    }

    /**
     * Register a new student account.
     * Only librarians should call this (enforced in the menu logic).
     *
     * Demonstrates:
     * - Input validation with String methods
     * - JDBC INSERT with PreparedStatement
     * - Exception handling
     */
    public static boolean registerStudent(String name, String password) {
        // String handling: validate that name is not blank
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Error: Student name cannot be empty.");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Error: Password cannot be empty.");
            return false;
        }

        // String handling: trim() removes accidental spaces
        String cleanName = name.trim();
        String hashedPassword = hashPassword(password.trim());

        String sql = "INSERT INTO users (name, password_hash, role) VALUES (?, ?, 'STUDENT')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cleanName);
            stmt.setString(2, hashedPassword);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        System.out.println("Student registered successfully!");
                        System.out.println("Student ID: " + newId + " (Use this ID to login)");
                        System.out.println("Name: " + cleanName);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error registering student: " + e.getMessage());
        }
        return false;
    }
}
