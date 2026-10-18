// LibraryService.java - Core business logic for the library system
// (Demonstrates: Interfaces implementation, JDBC operations, Exception handling,
//  ArrayList usage, String handling, Database transactions)
//
// This class implements the BookOperations interface, providing concrete
// implementations for all book-related operations using JDBC.

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LibraryService implements BookOperations {

    // ==================== BOOK OPERATIONS (Interface Implementation) ====================

    /**
     * Add a new book to the library.
     * Demonstrates: JDBC INSERT with PreparedStatement, input validation
     */
    @Override
    public boolean addBook(Book book) {
        // Validate input - String handling: trim() and isEmpty() checks
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            System.out.println("Error: Book title cannot be empty.");
            return false;
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            System.out.println("Error: Author name cannot be empty.");
            return false;
        }

        String sql = "INSERT INTO books (title, author, genre, isbn, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle().trim());
            stmt.setString(2, book.getAuthor().trim());
            stmt.setString(3, book.getGenre().trim());
            stmt.setString(4, book.getIsbn().trim());
            stmt.setInt(5, book.getTotalCopies());
            stmt.setInt(6, book.getAvailableCopies());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book added successfully: " + book.getTitle());
                return true;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("Error: A book with this ISBN already exists.");
            } else {
                System.out.println("Error adding book: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Retrieve all books from the database.
     * Demonstrates: ArrayList usage - we use ArrayList to temporarily store
     * Book objects retrieved from the database so we can display them.
     * The ArrayList is NOT the permanent store (MySQL is).
     */
    @Override
    public ArrayList<Book> getAllBooks() {
        // ArrayList<Book> temporarily holds results from the database query
        ArrayList<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Create Book object from each database row
                Book book = new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getString("isbn"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies")
                );
                books.add(book);  // ArrayList.add() appends the book to the list
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving books: " + e.getMessage());
        }

        return books;
    }

    /**
     * Search books by title or author using a keyword.
     * Demonstrates: String handling (case-insensitive search using SQL LIKE),
     *              ArrayList usage, JDBC
     */
    @Override
    public ArrayList<Book> searchBooks(String keyword) {
        ArrayList<Book> results = new ArrayList<>();

        // String handling: trim() removes whitespace, check for empty input
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Error: Search keyword cannot be empty.");
            return results;
        }

        // SQL LIKE with % wildcards performs case-insensitive partial matching
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) OR LOWER(author) LIKE LOWER(?) ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword.trim() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("isbn"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                    );
                    results.add(book);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching books: " + e.getMessage());
        }

        return results;
    }

    /**
     * Update a book's details.
     */
    @Override
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, genre = ?, isbn = ?, total_copies = ?, available_copies = ? WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle().trim());
            stmt.setString(2, book.getAuthor().trim());
            stmt.setString(3, book.getGenre().trim());
            stmt.setString(4, book.getIsbn().trim());
            stmt.setInt(5, book.getTotalCopies());
            stmt.setInt(6, book.getAvailableCopies());
            stmt.setInt(7, book.getBookId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book updated successfully.");
                return true;
            } else {
                System.out.println("Error: Book with ID " + book.getBookId() + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
        return false;
    }

    /**
     * Delete a book - only if no active loans or reservations exist for it.
     */
    @Override
    public boolean deleteBook(int bookId) {
        // First check if there are active loans or reservations
        String checkLoans = "SELECT COUNT(*) FROM loans WHERE book_id = ? AND return_date IS NULL";
        String checkReservations = "SELECT COUNT(*) FROM reservations WHERE book_id = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Check active loans
            try (PreparedStatement stmt = conn.prepareStatement(checkLoans)) {
                stmt.setInt(1, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Cannot delete: This book has active loans.");
                        return false;
                    }
                }
            }

            // Check active reservations
            try (PreparedStatement stmt = conn.prepareStatement(checkReservations)) {
                stmt.setInt(1, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Cannot delete: This book has active reservations.");
                        return false;
                    }
                }
            }

            // Safe to delete - also delete any historical loan/reservation records
            // Use transaction
            conn.setAutoCommit(false);
            try {
                // Delete historical loans
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM loans WHERE book_id = ?")) {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                }
                // Delete historical reservations
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM reservations WHERE book_id = ?")) {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                }
                // Delete the book
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE book_id = ?")) {
                    stmt.setInt(1, bookId);
                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        conn.commit();
                        System.out.println("Book deleted successfully.");
                        return true;
                    } else {
                        conn.rollback();
                        System.out.println("Error: Book with ID " + bookId + " not found.");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error deleting book: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    // ==================== BORROWING OPERATIONS ====================

    /**
     * Borrow a book.
     * Demonstrates: Database transactions (multiple related changes must all succeed or all fail)
     * - Decrease available_copies in books table
     * - Insert a record in loans table
     * Both must happen together (transaction), or neither should happen.
     */
    public boolean borrowBook(int userId, int bookId) {
        // First check if the book exists and is available
        String checkSql = "SELECT available_copies, title FROM books WHERE book_id = ?";
        // Check if user already has this book borrowed
        String checkLoanSql = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND book_id = ? AND return_date IS NULL";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Check if user already has this book
            try (PreparedStatement stmt = conn.prepareStatement(checkLoanSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("You have already borrowed this book. Return it first.");
                        return false;
                    }
                }
            }

            // Check availability
            String bookTitle = "";
            try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                stmt.setInt(1, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Error: Book with ID " + bookId + " not found.");
                        return false;
                    }
                    int available = rs.getInt("available_copies");
                    bookTitle = rs.getString("title");
                    if (available <= 0) {
                        System.out.println("Sorry, '" + bookTitle + "' is not available. All copies are borrowed.");
                        System.out.println("You can reserve this book using option 7 in the Student Menu.");
                        return false;
                    }
                }
            }

            // Use a TRANSACTION to ensure both operations succeed together
            conn.setAutoCommit(false);
            try {
                // Step 1: Decrease available copies
                String updateSql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ? AND available_copies > 0";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, bookId);
                    int updated = stmt.executeUpdate();
                    if (updated == 0) {
                        conn.rollback();
                        System.out.println("Sorry, the book just became unavailable.");
                        return false;
                    }
                }

                // Step 2: Create a loan record (14-day borrowing period)
                String loanSql = "INSERT INTO loans (user_id, book_id, borrow_date, due_date) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY))";
                try (PreparedStatement stmt = conn.prepareStatement(loanSql)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, bookId);
                    stmt.executeUpdate();
                }

                // Both operations succeeded - commit the transaction
                conn.commit();
                System.out.println("Book borrowed successfully!");
                System.out.println("Book: " + bookTitle);
                System.out.println("Due Date: " + LocalDate.now().plusDays(14));
                return true;

            } catch (SQLException e) {
                conn.rollback();  // Undo all changes if anything fails
                System.out.println("Error during borrowing: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Return a borrowed book.
     * Demonstrates: Database transactions, reservation processing
     * When a book is returned:
     * 1. Update the loan record with the return date
     * 2. Increase available_copies
     * 3. Check if anyone has reserved this book
     * 4. If yes, automatically process the first reservation and notify the student
     */
    public boolean returnBook(int userId, int bookId) {
        // Find the active loan
        String findLoan = "SELECT loan_id, book_id FROM loans WHERE user_id = ? AND book_id = ? AND return_date IS NULL";

        try (Connection conn = DatabaseConnection.getConnection()) {

            int loanId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(findLoan)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Error: You don't have this book borrowed.");
                        return false;
                    }
                    loanId = rs.getInt("loan_id");
                }
            }

            // Use transaction for return + reservation processing
            conn.setAutoCommit(false);
            try {
                // Step 1: Set the return date
                String returnSql = "UPDATE loans SET return_date = CURDATE() WHERE loan_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(returnSql)) {
                    stmt.setInt(1, loanId);
                    stmt.executeUpdate();
                }

                // Step 2: Increase available copies
                String updateSql = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                }

                // Step 3: Check for reservations (FIFO - first reservation_date, then reservation_id)
                String checkReservation = "SELECT reservation_id, user_id FROM reservations WHERE book_id = ? AND status = 'ACTIVE' ORDER BY reservation_date ASC, reservation_id ASC LIMIT 1";
                try (PreparedStatement stmt = conn.prepareStatement(checkReservation)) {
                    stmt.setInt(1, bookId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int reservationId = rs.getInt("reservation_id");
                            int reservedUserId = rs.getInt("user_id");

                            // Get book title for notification
                            String bookTitle = getBookTitleById(conn, bookId);

                            // Update reservation status to FULFILLED
                            String fulfillSql = "UPDATE reservations SET status = 'FULFILLED' WHERE reservation_id = ?";
                            try (PreparedStatement fulfillStmt = conn.prepareStatement(fulfillSql)) {
                                fulfillStmt.setInt(1, reservationId);
                                fulfillStmt.executeUpdate();
                            }

                            // Create notification for the reserved student
                            String notifySql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
                            try (PreparedStatement notifyStmt = conn.prepareStatement(notifySql)) {
                                notifyStmt.setInt(1, reservedUserId);
                                notifyStmt.setString(2, "Good news! The book '" + bookTitle + "' you reserved is now available. Please borrow it soon!");
                                notifyStmt.executeUpdate();
                            }

                            System.out.println("A reservation for this book has been fulfilled. The student has been notified.");
                        }
                    }
                }

                conn.commit();
                System.out.println("Book returned successfully! Thank you.");
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error during return: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Helper to get book title by ID (used within an existing connection/transaction)
     */
    private String getBookTitleById(Connection conn, int bookId) throws SQLException {
        String sql = "SELECT title FROM books WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("title");
                }
            }
        }
        return "Unknown";
    }

    // ==================== VIEW BORROWED BOOKS ====================

    /**
     * Get currently borrowed books for a student (not returned).
     * Demonstrates: ArrayList, JDBC JOIN query
     */
    public ArrayList<Loan> getBorrowedBooks(int userId) {
        ArrayList<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.loan_id, l.user_id, l.book_id, l.borrow_date, l.due_date, l.return_date, b.title " +
                     "FROM loans l JOIN books b ON l.book_id = b.book_id " +
                     "WHERE l.user_id = ? AND l.return_date IS NULL " +
                     "ORDER BY l.borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("user_id"),
                        rs.getInt("book_id"),
                        rs.getDate("borrow_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        null  // not returned
                    );
                    loan.setBookTitle(rs.getString("title"));
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving borrowed books: " + e.getMessage());
        }
        return loans;
    }

    /**
     * Get full borrowing history for a student (including returned books).
     */
    public ArrayList<Loan> getBorrowingHistory(int userId) {
        ArrayList<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.loan_id, l.user_id, l.book_id, l.borrow_date, l.due_date, l.return_date, b.title " +
                     "FROM loans l JOIN books b ON l.book_id = b.book_id " +
                     "WHERE l.user_id = ? ORDER BY l.borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date returnDateSql = rs.getDate("return_date");
                    LocalDate returnDate = (returnDateSql != null) ? returnDateSql.toLocalDate() : null;

                    Loan loan = new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("user_id"),
                        rs.getInt("book_id"),
                        rs.getDate("borrow_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        returnDate
                    );
                    loan.setBookTitle(rs.getString("title"));
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving history: " + e.getMessage());
        }
        return loans;
    }

    // ==================== ALL BORROWING RECORDS (Librarian) ====================

    /**
     * Get all borrowing records (for librarian view).
     */
    public ArrayList<Loan> getAllBorrowingRecords() {
        ArrayList<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.loan_id, l.user_id, l.book_id, l.borrow_date, l.due_date, l.return_date, " +
                     "b.title, u.name " +
                     "FROM loans l JOIN books b ON l.book_id = b.book_id " +
                     "JOIN users u ON l.user_id = u.user_id " +
                     "ORDER BY l.borrow_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date returnDateSql = rs.getDate("return_date");
                LocalDate returnDate = (returnDateSql != null) ? returnDateSql.toLocalDate() : null;

                Loan loan = new Loan(
                    rs.getInt("loan_id"),
                    rs.getInt("user_id"),
                    rs.getInt("book_id"),
                    rs.getDate("borrow_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate(),
                    returnDate
                );
                loan.setBookTitle(rs.getString("title"));
                loan.setStudentName(rs.getString("name"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
        return loans;
    }

    /**
     * Get overdue books (for librarian view).
     */
    public ArrayList<Loan> getOverdueBooks() {
        ArrayList<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.loan_id, l.user_id, l.book_id, l.borrow_date, l.due_date, l.return_date, " +
                     "b.title, u.name " +
                     "FROM loans l JOIN books b ON l.book_id = b.book_id " +
                     "JOIN users u ON l.user_id = u.user_id " +
                     "WHERE l.return_date IS NULL AND l.due_date < CURDATE() " +
                     "ORDER BY l.due_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("loan_id"),
                    rs.getInt("user_id"),
                    rs.getInt("book_id"),
                    rs.getDate("borrow_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate(),
                    null
                );
                loan.setBookTitle(rs.getString("title"));
                loan.setStudentName(rs.getString("name"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving overdue books: " + e.getMessage());
        }
        return loans;
    }

    // ==================== RESERVATION OPERATIONS ====================

    /**
     * Reserve a book that is currently unavailable.
     * Demonstrates: Input validation, duplicate check, JDBC INSERT
     */
    public boolean reserveBook(int userId, int bookId) {
        // Check if the book exists
        String checkBook = "SELECT available_copies, title FROM books WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            String bookTitle = "";
            try (PreparedStatement stmt = conn.prepareStatement(checkBook)) {
                stmt.setInt(1, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Error: Book with ID " + bookId + " not found.");
                        return false;
                    }
                    int available = rs.getInt("available_copies");
                    bookTitle = rs.getString("title");
                    if (available > 0) {
                        System.out.println("'" + bookTitle + "' is currently available. You can borrow it directly!");
                        return false;
                    }
                }
            }

            // Check for duplicate active reservation
            String checkDuplicate = "SELECT COUNT(*) FROM reservations WHERE user_id = ? AND book_id = ? AND status = 'ACTIVE'";
            try (PreparedStatement stmt = conn.prepareStatement(checkDuplicate)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("You already have an active reservation for this book.");
                        return false;
                    }
                }
            }

            // Create the reservation
            String insertSql = "INSERT INTO reservations (user_id, book_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Reservation placed successfully for '" + bookTitle + "'!");
                    System.out.println("You will be notified when a copy becomes available.");
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error placing reservation: " + e.getMessage());
        }
        return false;
    }

    // ==================== NOTIFICATION OPERATIONS ====================

    /**
     * Get unread notifications for a user.
     */
    public void viewNotifications(int userId) {
        String sql = "SELECT notification_id, message, created_at, is_read FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean hasNotifications = false;
                System.out.println("\n========== YOUR NOTIFICATIONS ==========");

                while (rs.next()) {
                    hasNotifications = true;
                    int id = rs.getInt("notification_id");
                    String message = rs.getString("message");
                    String createdAt = rs.getString("created_at");
                    boolean isRead = rs.getBoolean("is_read");

                    String readStatus = isRead ? "[Read]" : "[NEW]";
                    System.out.println(readStatus + " " + message + " (" + createdAt + ")");
                }

                if (!hasNotifications) {
                    System.out.println("No notifications.");
                }
            }

            // Mark all notifications as read
            String markRead = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
            try (PreparedStatement markStmt = conn.prepareStatement(markRead)) {
                markStmt.setInt(1, userId);
                markStmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving notifications: " + e.getMessage());
        }
    }

    // ==================== BOOK MATCH - RECOMMENDATION FEATURE ====================

    /**
     * Book Match: Recommend books based on a preferred genre.
     * This is a simple recommendation feature that:
     * 1. Asks the student to select a genre
     * 2. Queries the database for available books in that genre
     * 3. Displays recommendations with a friendly message
     *
     * Demonstrates: ArrayList, JDBC with parameterized queries, String operations
     */
    public ArrayList<Book> getBookRecommendations(String genre) {
        ArrayList<Book> recommendations = new ArrayList<>();

        // String handling: trim and validate
        if (genre == null || genre.trim().isEmpty()) {
            System.out.println("Error: Genre cannot be empty.");
            return recommendations;
        }

        // Case-insensitive genre matching using equalsIgnoreCase in SQL LOWER()
        String sql = "SELECT * FROM books WHERE LOWER(genre) = LOWER(?) AND available_copies > 0 ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, genre.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("isbn"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                    );
                    recommendations.add(book);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting recommendations: " + e.getMessage());
        }

        return recommendations;
    }

    /**
     * Get the list of distinct genres available in the library.
     */
    public ArrayList<String> getAvailableGenres() {
        ArrayList<String> genres = new ArrayList<>();
        String sql = "SELECT DISTINCT genre FROM books ORDER BY genre";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving genres: " + e.getMessage());
        }
        return genres;
    }

    /**
     * Get a book by its ID.
     */
    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("isbn"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving book: " + e.getMessage());
        }
        return null;
    }
}
