// Book.java - Represents a book in the library (Demonstrates: Encapsulation, Classes & Objects)
// All fields are private; accessed through public getter/setter methods.

/**
 * Book represents a book entity within the BookNest library system.
 *
 * OOP Concepts Demonstrated:
 * 1. CLASSES & OBJECTS: A blueprint defining attributes and behaviors of book instances.
 * 2. ENCAPSULATION: All member variables are private to prevent unauthorized direct modification.
 *    Access and mutation are provided through public getter and setter methods.
 * 3. CONSTRUCTOR OVERLOADING: Multiple constructors provide flexibility when instantiating books
 *    (e.g., creating a new book before database auto-increment ID vs. loading an existing record).
 */
public class Book {

    // Encapsulation: Private attributes
    private int bookId;
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private int totalCopies;
    private int availableCopies;

    /**
     * Default constructor.
     */
    public Book() {
    }

    /**
     * Constructor without ID (used when creating a new book before DB insertion).
     * The database generates the primary key auto-increment ID.
     * When a new book is added, all copies are initially available.
     *
     * @param title       Title of the book
     * @param author      Author of the book
     * @param genre       Genre / category of the book
     * @param isbn        Unique ISBN number
     * @param totalCopies Total number of copies owned
     */
    public Book(String title, String author, String genre, String isbn, int totalCopies) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies; // New book: all copies available
    }

    /**
     * Full constructor (used when retrieving existing books from the database).
     *
     * @param bookId          Unique ID of the book in the database
     * @param title           Title of the book
     * @param author          Author of the book
     * @param genre           Genre / category of the book
     * @param isbn            Unique ISBN number
     * @param totalCopies     Total number of copies owned
     * @param availableCopies Current number of copies available to borrow
     */
    public Book(int bookId, String title, String author, String genre, String isbn, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // ==========================================
    // Getters and Setters (Encapsulation)
    // ==========================================

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    // ==========================================
    // Helper & Display Methods
    // ==========================================

    /**
     * Checks if at least one copy of this book is currently available for borrowing.
     *
     * @return true if availableCopies > 0, false otherwise
     */
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /**
     * Displays formatted details of the book to the console.
     */
    public void displayDetails() {
        System.out.println("----------------------------------");
        System.out.println("Book ID         : " + bookId);
        System.out.println("Title           : " + title);
        System.out.println("Author          : " + author);
        System.out.println("Genre           : " + genre);
        System.out.println("ISBN            : " + isbn);
        System.out.println("Total Copies    : " + totalCopies);
        System.out.println("Available Copies: " + availableCopies);
        System.out.println("Status          : " + (isAvailable() ? "Available" : "All copies borrowed"));
        System.out.println("----------------------------------");
    }

    /**
     * Returns a compact string summary of the book.
     *
     * @return Formatted summary string
     */
    @Override
    public String toString() {
        return "[" + bookId + "] " + title + " by " + author + " (" + genre + ") - " + (isAvailable() ? availableCopies + " available" : "Unavailable");
    }
}
