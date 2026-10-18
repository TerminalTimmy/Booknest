// BookOperations.java - Interface for book-related operations (Demonstrates: Interfaces)
// Defines a contract that any class managing books must follow.
// LibraryService implements this interface.

import java.util.ArrayList;

/**
 * BookOperations defines the contract for operations performed on library books.
 *
 * OOP Concepts Demonstrated:
 * 1. INTERFACE: An interface specifies a set of abstract method signatures without implementations.
 *    Any class implementing this interface (e.g., LibraryService) agrees to provide concrete implementations
 *    for all declared methods.
 * 2. POLYMORPHISM: Allows caller code to interact with the interface reference rather than concrete implementation details.
 */
public interface BookOperations {

    /**
     * Add a new book to the library database.
     *
     * @param book The Book object containing details to insert
     * @return true if insertion was successful, false otherwise
     */
    boolean addBook(Book book);

    /**
     * Retrieve all books currently in the library catalog.
     *
     * @return ArrayList of Book objects
     */
    ArrayList<Book> getAllBooks();

    /**
     * Search books by keyword matching title or author (case-insensitive).
     *
     * @param keyword Search term entered by user
     * @return ArrayList of matching Book objects
     */
    ArrayList<Book> searchBooks(String keyword);

    /**
     * Update an existing book's details in the database.
     *
     * @param book The Book object containing updated information
     * @return true if update succeeded, false otherwise
     */
    boolean updateBook(Book book);

    /**
     * Delete a book from the library catalog by its ID.
     *
     * @param bookId The unique identifier of the book to remove
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteBook(int bookId);
}
