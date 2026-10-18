// Loan.java - Represents a book borrowing record (Demonstrates: Classes & Objects, Encapsulation)
// Tracks loan transactions including borrow dates, due dates, return status, and join data.

import java.time.LocalDate;

/**
 * Loan represents a book borrowing transaction in BookNest.
 *
 * OOP Concepts Demonstrated:
 * 1. CLASSES & OBJECTS: Encapsulates state and logic of borrowing operations.
 * 2. ENCAPSULATION: Member variables are marked private with controlled public accessors.
 * 3. COMPOSITION / JOIN FIELDS: Contains studentName and bookTitle for convenient presentation
 *    when joining data across users and books tables.
 */
public class Loan {

    // Encapsulation: Private database columns
    private int loanId;
    private int userId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null if not yet returned

    // Display fields (not columns in loans table, populated from SQL JOIN queries)
    private String bookTitle;
    private String studentName;

    /**
     * Default constructor.
     */
    public Loan() {
    }

    /**
     * Constructor for creating a new loan (before DB insertion, loanId auto-incremented by MySQL).
     *
     * @param userId     ID of the borrowing student
     * @param bookId     ID of the borrowed book
     * @param borrowDate Date on which the book was borrowed
     * @param dueDate    Date by which the book must be returned
     */
    public Loan(int userId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    /**
     * Full constructor for existing loans fetched from the database.
     *
     * @param loanId     Unique identifier of the loan record
     * @param userId     ID of the borrowing student
     * @param bookId     ID of the borrowed book
     * @param borrowDate Date on which the book was borrowed
     * @param dueDate    Date by which the book must be returned
     * @param returnDate Date on which the book was returned (null if active)
     */
    public Loan(int loanId, int userId, int bookId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        this.loanId = loanId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    /**
     * Full constructor including display fields from JOIN operations.
     *
     * @param loanId      Unique identifier of the loan record
     * @param userId      ID of the borrowing student
     * @param bookId      ID of the borrowed book
     * @param borrowDate  Date on which the book was borrowed
     * @param dueDate     Date by which the book must be returned
     * @param returnDate  Date on which the book was returned (null if active)
     * @param bookTitle   Title of the borrowed book
     * @param studentName Name of the student who borrowed the book
     */
    public Loan(int loanId, int userId, int bookId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, String bookTitle, String studentName) {
        this.loanId = loanId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.bookTitle = bookTitle;
        this.studentName = studentName;
    }

    // ==========================================
    // Getters and Setters (Encapsulation)
    // ==========================================

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    // ==========================================
    // Status & Utility Methods
    // ==========================================

    /**
     * Checks if the book has been returned.
     *
     * @return true if returnDate is not null, false otherwise
     */
    public boolean isReturned() {
        return returnDate != null;
    }

    /**
     * Checks if the loan is currently overdue.
     * A loan is overdue if it has not been returned and today's date is after the due date.
     *
     * @return true if active and past due date, false otherwise
     */
    public boolean isOverdue() {
        return !isReturned() && dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Returns a formatted string representation of the loan.
     *
     * @return Loan status line
     */
    @Override
    public String toString() {
        String status = isReturned() ? "Returned on " + returnDate : (isOverdue() ? "OVERDUE" : "Active");
        return "Loan #" + loanId + " | Book: " + (bookTitle != null ? bookTitle : "ID:" + bookId) +
               " | Borrowed: " + borrowDate + " | Due: " + dueDate + " | Status: " + status;
    }
}
