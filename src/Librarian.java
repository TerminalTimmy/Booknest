// Librarian.java - Represents a librarian user (Demonstrates: Inheritance, Polymorphism)
// Extends the abstract User class and provides librarian-specific behavior.

/**
 * Librarian represents an administrative staff member in BookNest.
 *
 * OOP Concepts Demonstrated:
 * 1. INHERITANCE: Extends the abstract User class to inherit all common user properties.
 *    Uses 'super(...)' to pass parameters up to the User parent constructor with the role "LIBRARIAN".
 * 2. POLYMORPHISM (Method Overriding):
 *    - displayMenu(): Implements User's abstract displayMenu() with administrative operations.
 *    - getWelcomeMessage(): Overrides User's getWelcomeMessage() to provide a custom administrative greeting.
 */
public class Librarian extends User {

    /**
     * Parameterized constructor for Librarian.
     * Uses super() to invoke the User constructor, setting role automatically to "LIBRARIAN".
     *
     * @param userId        Unique identifier for the librarian
     * @param name          Full name of the librarian
     * @param passwordHash  SHA-256 hashed password
     */
    public Librarian(int userId, String name, String passwordHash) {
        super(userId, name, passwordHash, "LIBRARIAN");
    }

    /**
     * Overriding abstract method from User (Polymorphism).
     * Displays administrative management options available to a librarian.
     */
    @Override
    public void displayMenu() {
        System.out.println("\n========== LIBRARIAN MENU ==========");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Books");
        System.out.println("4. Update Book");
        System.out.println("5. Delete Book");
        System.out.println("6. Register Student");
        System.out.println("7. View Borrowing Records");
        System.out.println("8. View Overdue Books");
        System.out.println("9. View Library Summary");
        System.out.println("10. Logout");
        System.out.print("Enter your choice: ");
    }

    /**
     * Overriding getWelcomeMessage from User (Polymorphism: method overriding).
     *
     * @return Librarian-specific welcome message
     */
    @Override
    public String getWelcomeMessage() {
        return "Welcome, Librarian " + getName() + "! Managing the library today.";
    }
}
