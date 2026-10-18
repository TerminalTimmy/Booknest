// User.java - Abstract base class for all users (Demonstrates: Abstraction, Inheritance)
// This class cannot be instantiated directly. Student and Librarian extend it.

/**
 * User is an abstract class that serves as the base for all types of users in BookNest.
 * 
 * OOP Concepts Demonstrated:
 * 1. ABSTRACTION: An abstract class provides a template and cannot be instantiated with 'new User(...)'.
 *    The abstract method displayMenu() enforces that every specific user type provides its own menu.
 * 2. ENCAPSULATION: All member variables are private, protecting the internal state.
 *    Access and modifications are strictly managed via public getter and setter methods.
 * 3. INHERITANCE: Common user attributes (userId, name, passwordHash, role) are defined here once,
 *    and inherited by child classes (Student and Librarian) without duplicating code.
 */
public abstract class User {

    // Encapsulation: Private member variables protect data from direct modification
    private int userId;
    private String name;
    private String passwordHash;
    private String role;

    /**
     * Parameterized constructor to initialize a User object.
     * Subclasses invoke this constructor using super(...).
     *
     * @param userId        Unique identifier for the user
     * @param name          Full name of the user
     * @param passwordHash  SHA-256 hashed password
     * @param role          Role of the user (e.g., "STUDENT", "LIBRARIAN")
     */
    public User(int userId, String name, String passwordHash, String role) {
        this.userId = userId;
        this.name = name;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ==========================================
    // Getters and Setters (Encapsulation)
    // ==========================================

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // ==========================================
    // Abstract Method (Abstraction)
    // ==========================================

    /**
     * Abstract method to display the user-specific console menu.
     * Every subclass must implement this method according to its permissions and actions.
     */
    public abstract void displayMenu();

    // ==========================================
    // Concrete Method (Polymorphism Base)
    // ==========================================

    /**
     * Concrete welcome message method.
     * Can be overridden by subclasses to provide customized welcome messages (Polymorphism).
     *
     * @return Welcome greeting string
     */
    public String getWelcomeMessage() {
        return "Welcome to BookNest, " + name + "!";
    }

    /**
     * Overrides java.lang.Object.toString() to return a formatted string representation of the user.
     *
     * @return Formatted string with role, name, and user ID
     */
    @Override
    public String toString() {
        return role + ": " + name + " (ID: " + userId + ")";
    }
}
