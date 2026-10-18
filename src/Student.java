// Student.java - Represents a student user (Demonstrates: Inheritance, Polymorphism)
// Extends the abstract User class and provides student-specific behavior.

/**
 * Student represents a student member in BookNest.
 *
 * OOP Concepts Demonstrated:
 * 1. INHERITANCE: The Student class extends the abstract User class using the 'extends' keyword.
 *    It inherits all attributes (userId, name, passwordHash, role) and public methods of User.
 *    The constructor uses 'super(...)' to pass data to the parent class constructor.
 * 2. POLYMORPHISM (Method Overriding):
 *    - displayMenu(): Implements the abstract method from User with student-specific menu options.
 *    - getWelcomeMessage(): Overrides the parent method to provide a customized greeting for students.
 */
public class Student extends User {

    /**
     * Parameterized constructor for Student.
     * Uses super() to invoke User's constructor, setting role automatically to "STUDENT".
     *
     * @param userId        Unique identifier for the student
     * @param name          Full name of the student
     * @param passwordHash  SHA-256 hashed password
     */
    public Student(int userId, String name, String passwordHash) {
        super(userId, name, passwordHash, "STUDENT");
    }

    /**
     * Overriding abstract method from User (Polymorphism).
     * Displays available menu options for a student.
     */
    @Override
    public void displayMenu() {
        System.out.println("\n========== STUDENT MENU ==========");
        System.out.println("1. View All Books");
        System.out.println("2. Search Books");
        System.out.println("3. Borrow a Book");
        System.out.println("4. Return a Book");
        System.out.println("5. View My Borrowed Books");
        System.out.println("6. View Borrowing History");
        System.out.println("7. Reserve an Unavailable Book");
        System.out.println("8. View Notifications");
        System.out.println("9. Book Match - Recommendations");
        System.out.println("10. Logout");
        System.out.print("Enter your choice: ");
    }

    /**
     * Overriding getWelcomeMessage from User (Polymorphism: method overriding).
     *
     * @return Student-specific welcome message
     */
    @Override
    public String getWelcomeMessage() {
        return "Welcome, Student " + getName() + "! Ready to explore books?";
    }
}
