// Main.java - Entry point for the BookNest Library Management System
// This class handles the console menu interface and user interaction.
// Demonstrates: Scanner input, menu-driven flow, exception handling, input validation

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    // Shared Scanner for reading console input
    private static Scanner scanner = new Scanner(System.in);
    // LibraryService handles all database operations
    private static LibraryService libraryService = new LibraryService();

    public static void main(String[] args) {
        System.out.println("\n============================================");
        System.out.println("     Welcome to BookNest Library System");
        System.out.println("     Running on Java " + System.getProperty("java.version"));
        System.out.println("============================================");

        // Test database connection on startup
        if (!DatabaseConnection.testConnection()) {
            System.out.println("\nFATAL: Cannot connect to database. Please check:");
            System.out.println("1. MySQL server is running");
            System.out.println("2. Database 'booknest_db' exists (run schema.sql)");
            System.out.println("3. Username and password in DatabaseConnection.java are correct");
            return;
        }

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    handleStudentLogin();
                    break;
                case 2:
                    handleLibrarianLogin();
                    break;
                case 3:
                    System.out.println("\nThank you for using BookNest! Goodbye.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n========== BOOKNEST ==========");
        System.out.println("1. Student Login");
        System.out.println("2. Librarian Login");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    // ==================== LOGIN HANDLERS ====================

    private static void handleStudentLogin() {
        System.out.println("\n--- Student Login ---");
        System.out.print("Enter Student ID: ");
        int userId = getIntInput();
        if (userId == -1) {
            System.out.println("Invalid ID. Please enter a number.");
            return;
        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = AuthService.login(userId, password);
        if (user == null) {
            System.out.println("Login failed! Invalid Student ID or password.");
            return;
        }

        if (!user.getRole().equalsIgnoreCase("STUDENT")) {
            System.out.println("This is not a student account. Please use Librarian Login.");
            return;
        }

        // Polymorphism: getWelcomeMessage() calls Student's overridden version
        System.out.println("\n" + user.getWelcomeMessage());
        studentMenu((Student) user);
    }

    private static void handleLibrarianLogin() {
        System.out.println("\n--- Librarian Login ---");
        System.out.print("Enter Librarian ID: ");
        int userId = getIntInput();
        if (userId == -1) {
            System.out.println("Invalid ID. Please enter a number.");
            return;
        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = AuthService.login(userId, password);
        if (user == null) {
            System.out.println("Login failed! Invalid Librarian ID or password.");
            return;
        }

        if (!user.getRole().equalsIgnoreCase("LIBRARIAN")) {
            System.out.println("This is not a librarian account. Please use Student Login.");
            return;
        }

        System.out.println("\n" + user.getWelcomeMessage());
        librarianMenu((Librarian) user);
    }

    // ==================== STUDENT MENU ====================

    private static void studentMenu(Student student) {
        boolean loggedIn = true;
        while (loggedIn) {
            // Polymorphism: displayMenu() calls Student's version
            student.displayMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1: viewAllBooks(); break;
                case 2: searchBooks(); break;
                case 3: borrowBook(student); break;
                case 4: returnBook(student); break;
                case 5: viewBorrowedBooks(student); break;
                case 6: viewBorrowingHistory(student); break;
                case 7: reserveBook(student); break;
                case 8: viewNotifications(student); break;
                case 9: bookMatchRecommendations(); break;
                case 10:
                    System.out.println("Logged out successfully.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-10.");
            }
        }
    }

    // ==================== LIBRARIAN MENU ====================

    private static void librarianMenu(Librarian librarian) {
        boolean loggedIn = true;
        while (loggedIn) {
            librarian.displayMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1: addBook(); break;
                case 2: viewAllBooks(); break;
                case 3: searchBooks(); break;
                case 4: updateBook(); break;
                case 5: deleteBook(); break;
                case 6: registerStudent(); break;
                case 7: viewAllBorrowingRecords(); break;
                case 8: viewOverdueBooks(); break;
                case 9: viewLibrarySummary(); break;
                case 10:
                    System.out.println("Logged out successfully.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-10.");
            }
        }
    }

    // ==================== SHARED OPERATIONS ====================

    private static void viewAllBooks() {
        System.out.println("\n========== ALL BOOKS ==========");
        // ArrayList<Book> - temporarily stores books from the database for display
        ArrayList<Book> books = libraryService.getAllBooks();

        if (books.isEmpty()) {  // ArrayList.isEmpty() checks if the list has no elements
            System.out.println("No books found in the library.");
            return;
        }

        // Iterate through the ArrayList using a for-each loop
        System.out.println(String.format("%-5s %-35s %-25s %-15s %-5s", "ID", "Title", "Author", "Genre", "Avail"));
        System.out.println("-".repeat(85));
        for (Book book : books) {
            System.out.println(String.format("%-5d %-35s %-25s %-15s %d/%d",
                book.getBookId(),
                truncate(book.getTitle(), 33),
                truncate(book.getAuthor(), 23),
                book.getGenre(),
                book.getAvailableCopies(),
                book.getTotalCopies()));
        }
        System.out.println("\nTotal: " + books.size() + " book(s)");  // ArrayList.size() returns count
    }

    private static void searchBooks() {
        System.out.print("\nEnter search keyword (title or author): ");
        String keyword = scanner.nextLine();

        // String handling: trim() removes leading/trailing whitespace
        if (keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return;
        }

        ArrayList<Book> results = libraryService.searchBooks(keyword);

        if (results.isEmpty()) {
            System.out.println("No books found matching '" + keyword.trim() + "'.");
            return;
        }

        System.out.println("\nSearch Results for '" + keyword.trim() + "':");
        System.out.println(String.format("%-5s %-35s %-25s %-15s %-5s", "ID", "Title", "Author", "Genre", "Avail"));
        System.out.println("-".repeat(85));
        for (Book book : results) {
            System.out.println(String.format("%-5d %-35s %-25s %-15s %d/%d",
                book.getBookId(),
                truncate(book.getTitle(), 33),
                truncate(book.getAuthor(), 23),
                book.getGenre(),
                book.getAvailableCopies(),
                book.getTotalCopies()));
        }
        System.out.println("Found: " + results.size() + " book(s)");
    }

    // ==================== STUDENT-SPECIFIC OPERATIONS ====================

    private static void borrowBook(Student student) {
        System.out.println("\n--- Borrow a Book ---");
        viewAllBooks();
        System.out.print("\nEnter Book ID to borrow: ");
        int bookId = getIntInput();
        if (bookId == -1) {
            System.out.println("Invalid Book ID.");
            return;
        }
        libraryService.borrowBook(student.getUserId(), bookId);
    }

    private static void returnBook(Student student) {
        System.out.println("\n--- Return a Book ---");
        viewBorrowedBooks(student);
        System.out.print("\nEnter Book ID to return: ");
        int bookId = getIntInput();
        if (bookId == -1) {
            System.out.println("Invalid Book ID.");
            return;
        }
        libraryService.returnBook(student.getUserId(), bookId);
    }

    private static void viewBorrowedBooks(Student student) {
        System.out.println("\n========== MY BORROWED BOOKS ==========");
        ArrayList<Loan> loans = libraryService.getBorrowedBooks(student.getUserId());

        if (loans.isEmpty()) {
            System.out.println("You have no books currently borrowed.");
            return;
        }

        for (Loan loan : loans) {
            System.out.println(loan);
        }
        System.out.println("Total borrowed: " + loans.size());
    }

    private static void viewBorrowingHistory(Student student) {
        System.out.println("\n========== BORROWING HISTORY ==========");
        ArrayList<Loan> loans = libraryService.getBorrowingHistory(student.getUserId());

        if (loans.isEmpty()) {
            System.out.println("No borrowing history found.");
            return;
        }

        for (Loan loan : loans) {
            System.out.println(loan);
        }
    }

    private static void reserveBook(Student student) {
        System.out.println("\n--- Reserve an Unavailable Book ---");
        viewAllBooks();
        System.out.print("\nEnter Book ID to reserve: ");
        int bookId = getIntInput();
        if (bookId == -1) {
            System.out.println("Invalid Book ID.");
            return;
        }
        libraryService.reserveBook(student.getUserId(), bookId);
    }

    private static void viewNotifications(Student student) {
        libraryService.viewNotifications(student.getUserId());
    }

    private static void bookMatchRecommendations() {
        System.out.println("\n========== BOOK MATCH - Recommendations ==========");
        System.out.println("Select a genre to get book recommendations:\n");

        // Get available genres from the database
        ArrayList<String> genres = libraryService.getAvailableGenres();

        if (genres.isEmpty()) {
            System.out.println("No genres available.");
            return;
        }

        // Display genres with numbers for easy selection
        for (int i = 0; i < genres.size(); i++) {
            System.out.println((i + 1) + ". " + genres.get(i));  // ArrayList.get(index) retrieves element
        }
        System.out.print("\nEnter genre number: ");
        int genreChoice = getIntInput();

        // Validate the choice
        if (genreChoice < 1 || genreChoice > genres.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        String selectedGenre = genres.get(genreChoice - 1);
        System.out.println("\nShowing recommendations for genre: " + selectedGenre);

        ArrayList<Book> recommendations = libraryService.getBookRecommendations(selectedGenre);

        if (recommendations.isEmpty()) {
            System.out.println("No available books in this genre right now.");
            System.out.println("Try another genre or check back later!");
            return;
        }

        System.out.println("\n" + String.format("%-5s %-35s %-25s %-5s", "ID", "Title", "Author", "Avail"));
        System.out.println("-".repeat(70));
        for (Book book : recommendations) {
            System.out.println(String.format("%-5d %-35s %-25s %d/%d",
                book.getBookId(),
                truncate(book.getTitle(), 33),
                truncate(book.getAuthor(), 23),
                book.getAvailableCopies(),
                book.getTotalCopies()));
        }
        System.out.println("\n>> Recommended because they match your preferred genre: " + selectedGenre);
        System.out.println(">> Found " + recommendations.size() + " available book(s) in this genre.");
    }

    // ==================== LIBRARIAN-SPECIFIC OPERATIONS ====================

    private static void addBook() {
        System.out.println("\n--- Add New Book ---");

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        // String handling: validate and trim
        if (title.trim().isEmpty()) {
            System.out.println("Error: Title cannot be empty.");
            return;
        }

        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        if (author.trim().isEmpty()) {
            System.out.println("Error: Author cannot be empty.");
            return;
        }

        System.out.print("Enter Genre (e.g., Technology, Mystery, Science Fiction, Literature, History): ");
        String genre = scanner.nextLine();
        if (genre.trim().isEmpty()) {
            System.out.println("Error: Genre cannot be empty.");
            return;
        }

        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        if (isbn.trim().isEmpty()) {
            System.out.println("Error: ISBN cannot be empty.");
            return;
        }

        System.out.print("Enter Number of Copies: ");
        int copies = getIntInput();
        if (copies <= 0) {
            System.out.println("Error: Number of copies must be at least 1.");
            return;
        }

        Book book = new Book(title.trim(), author.trim(), genre.trim(), isbn.trim(), copies);
        libraryService.addBook(book);
    }

    private static void updateBook() {
        System.out.println("\n--- Update Book ---");
        viewAllBooks();
        System.out.print("\nEnter Book ID to update: ");
        int bookId = getIntInput();
        if (bookId == -1) {
            System.out.println("Invalid Book ID.");
            return;
        }

        Book existing = libraryService.getBookById(bookId);
        if (existing == null) {
            System.out.println("Book not found.");
            return;
        }

        System.out.println("\nCurrent details:");
        existing.displayDetails();

        System.out.println("\nEnter new values (press Enter to keep current value):");

        System.out.print("Title [" + existing.getTitle() + "]: ");
        String title = scanner.nextLine();
        if (!title.trim().isEmpty()) existing.setTitle(title.trim());

        System.out.print("Author [" + existing.getAuthor() + "]: ");
        String author = scanner.nextLine();
        if (!author.trim().isEmpty()) existing.setAuthor(author.trim());

        System.out.print("Genre [" + existing.getGenre() + "]: ");
        String genre = scanner.nextLine();
        if (!genre.trim().isEmpty()) existing.setGenre(genre.trim());

        System.out.print("ISBN [" + existing.getIsbn() + "]: ");
        String isbn = scanner.nextLine();
        if (!isbn.trim().isEmpty()) existing.setIsbn(isbn.trim());

        System.out.print("Total Copies [" + existing.getTotalCopies() + "]: ");
        String copiesStr = scanner.nextLine();
        if (!copiesStr.trim().isEmpty()) {
            try {
                int newTotal = Integer.parseInt(copiesStr.trim());
                if (newTotal < 0) {
                    System.out.println("Copies cannot be negative.");
                    return;
                }
                // Adjust available copies proportionally
                int diff = newTotal - existing.getTotalCopies();
                int newAvailable = existing.getAvailableCopies() + diff;
                if (newAvailable < 0) newAvailable = 0;
                existing.setTotalCopies(newTotal);
                existing.setAvailableCopies(newAvailable);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Keeping current value.");
            }
        }

        libraryService.updateBook(existing);
    }

    private static void deleteBook() {
        System.out.println("\n--- Delete Book ---");
        viewAllBooks();
        System.out.print("\nEnter Book ID to delete: ");
        int bookId = getIntInput();
        if (bookId == -1) {
            System.out.println("Invalid Book ID.");
            return;
        }

        System.out.print("Are you sure you want to delete this book? (yes/no): ");
        String confirm = scanner.nextLine();
        // String handling: equalsIgnoreCase() for case-insensitive comparison
        if (confirm.trim().equalsIgnoreCase("yes")) {
            libraryService.deleteBook(bookId);
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void registerStudent() {
        System.out.println("\n--- Register New Student ---");
        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        AuthService.registerStudent(name, password);
    }

    private static void viewAllBorrowingRecords() {
        System.out.println("\n========== ALL BORROWING RECORDS ==========");
        ArrayList<Loan> loans = libraryService.getAllBorrowingRecords();

        if (loans.isEmpty()) {
            System.out.println("No borrowing records found.");
            return;
        }

        for (Loan loan : loans) {
            System.out.println(loan + " | Student: " + loan.getStudentName());
        }
        System.out.println("\nTotal records: " + loans.size());
    }

    private static void viewOverdueBooks() {
        System.out.println("\n========== OVERDUE BOOKS ==========");
        ArrayList<Loan> overdueLoans = libraryService.getOverdueBooks();

        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue books. All returns are on time!");
            return;
        }

        for (Loan loan : overdueLoans) {
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), java.time.LocalDate.now());
            System.out.println(loan + " | Student: " + loan.getStudentName() + " | " + daysOverdue + " days overdue");
        }
        System.out.println("\nTotal overdue: " + overdueLoans.size());
    }

    private static void viewLibrarySummary() {
        System.out.println("\n--- Generating Library Summary ---");
        // Multithreading: Create a separate thread to generate the report
        // This demonstrates how a background task can run independently
        ReportGenerator report = new ReportGenerator();
        report.setName("BookNest-Report-Thread");
        report.start();  // Start the thread - run() executes in a new thread

        try {
            // Wait for the report thread to finish before continuing
            // join() makes the main thread wait until the report thread completes
            report.join();
        } catch (InterruptedException e) {
            System.out.println("Report generation was interrupted.");
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Safely read an integer from console input.
     * Demonstrates: Exception handling with try-catch for NumberFormatException
     * Returns -1 if input is not a valid integer.
     */
    private static int getIntInput() {
        try {
            String input = scanner.nextLine();
            // String handling: trim() removes accidental whitespace before parsing
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            // The user entered something that is not a number
            return -1;
        }
    }

    /**
     * Truncate a string to a maximum length for clean table display.
     * Demonstrates: String handling with substring()
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 2) + "..";
    }
}
