// ReportGenerator.java - Generates a library summary report in a background thread
// (Demonstrates: Multithreading with basic Java Thread)
//
// PURPOSE OF MULTITHREADING HERE:
// In a real library system, generating reports (counting books, loans, overdue items)
// could take time if the database is large. By running the report generation in a
// separate thread, the main program can continue to respond to user input while
// the report is being prepared.
//
// For our small database, this runs almost instantly, but it demonstrates the
// concept of creating and running a separate thread.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportGenerator extends Thread {

    // We extend Thread and override the run() method.
    // When start() is called, this run() method executes in a NEW thread.

    @Override
    public void run() {
        System.out.println("\n[Report Thread] Generating library summary report...");
        System.out.println("[Report Thread] Thread Name: " + Thread.currentThread().getName());

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Count total books
            int totalBooks = countQuery(conn, "SELECT COUNT(*) FROM books");

            // Count total available copies
            int availableCopies = countQuery(conn, "SELECT SUM(available_copies) FROM books");

            // Count active loans (not returned)
            int activeLoans = countQuery(conn, "SELECT COUNT(*) FROM loans WHERE return_date IS NULL");

            // Count overdue loans
            int overdueLoans = countQuery(conn, "SELECT COUNT(*) FROM loans WHERE return_date IS NULL AND due_date < CURDATE()");

            // Count total students
            int totalStudents = countQuery(conn, "SELECT COUNT(*) FROM users WHERE role = 'STUDENT'");

            // Count active reservations
            int activeReservations = countQuery(conn, "SELECT COUNT(*) FROM reservations WHERE status = 'ACTIVE'");

            // Display the report
            System.out.println("\n============ LIBRARY SUMMARY REPORT ============");
            System.out.println("  Total Book Titles     : " + totalBooks);
            System.out.println("  Available Copies      : " + availableCopies);
            System.out.println("  Registered Students   : " + totalStudents);
            System.out.println("  Active Loans          : " + activeLoans);
            System.out.println("  Overdue Loans         : " + overdueLoans);
            System.out.println("  Active Reservations   : " + activeReservations);
            System.out.println("=================================================");
            System.out.println("[Report Thread] Report generation complete.");

        } catch (SQLException e) {
            System.out.println("[Report Thread] Error generating report: " + e.getMessage());
        }
    }

    /**
     * Helper method to execute a COUNT or SUM query and return the result.
     */
    private int countQuery(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}
