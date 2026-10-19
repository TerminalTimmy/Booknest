# BookNest — OOP Concepts Explained

This document maps every required OOP concept to the exact source file and method where it is demonstrated in the BookNest project.

---

## 1. Classes and Objects

**Concept:** A class is a blueprint; an object is an instance created from that blueprint.

| Class | File | Purpose |
|-------|------|---------|
| `Book` | [Book.java](src/Book.java) | Represents a book entity with attributes and behaviors |
| `Student` | [Student.java](src/Student.java) | Represents a student user |
| `Librarian` | [Librarian.java](src/Librarian.java) | Represents a librarian user |
| `Loan` | [Loan.java](src/Loan.java) | Represents a borrowing transaction |

**Example:** In `Main.java`, when a user adds a book:
```java
Book book = new Book(title, author, genre, isbn, copies);  // Creating an object
libraryService.addBook(book);  // Using the object
```

---

## 2. Encapsulation

**Concept:** Hide internal data using `private` fields and provide controlled access through `public` getter/setter methods.

**Demonstrated in:** Every model class (`Book.java`, `User.java`, `Loan.java`)

**Example in `Book.java`:**
```java
private String title;        // Private field — cannot be accessed directly from outside

public String getTitle() {   // Public getter — controlled read access
    return title;
}

public void setTitle(String title) {  // Public setter — controlled write access
    this.title = title;
}
```

**Why it matters:** If `title` were `public`, any code could set it to `null` or an empty string. With encapsulation, we can add validation inside the setter.

---

## 3. Inheritance

**Concept:** A child class extends a parent class, inheriting its fields and methods. This avoids code duplication.

**Demonstrated in:** `User.java` → `Student.java` and `Librarian.java`

```
User (abstract parent class)
├── Student (child class)
└── Librarian (child class)
```

**Key code in `Student.java`:**
```java
public class Student extends User {           // 'extends' keyword for inheritance
    public Student(int userId, String name, String passwordHash) {
        super(userId, name, passwordHash, "STUDENT");  // 'super()' calls parent constructor
    }
}
```

**What is inherited:**
- `userId`, `name`, `passwordHash`, `role` fields (from `User`)
- `getUserId()`, `getName()`, `getRole()` methods (from `User`)
- `toString()` method (from `User`)

---

## 4. Polymorphism (Method Overriding)

**Concept:** A child class provides its own implementation of a method defined in the parent class. The same method call produces different behavior depending on the actual object type.

**Demonstrated in:** `displayMenu()` and `getWelcomeMessage()` in `Student.java` and `Librarian.java`

**How it works in `Main.java`:**
```java
User user = AuthService.login(userId, password);  // Returns Student or Librarian
System.out.println(user.getWelcomeMessage());     // Calls the CORRECT overridden version

// If user is a Student → "Welcome, Student Rahul! Ready to explore books?"
// If user is a Librarian → "Welcome, Librarian Admin! Managing the library today."
```

**`displayMenu()` is also polymorphic:**
- `student.displayMenu()` → Shows 10 student menu options
- `librarian.displayMenu()` → Shows 10 librarian menu options
- Both are declared as `abstract` in `User.java`, forcing each subclass to implement its own version

---

## 5. Abstraction

**Concept:** Hide complex implementation details and show only essential features. In Java, this is achieved through `abstract` classes and methods.

**Demonstrated in:** `User.java`

```java
public abstract class User {            // 'abstract' — cannot create: new User(...)
    public abstract void displayMenu(); // 'abstract method' — no body, must be implemented by subclasses
    
    public String getWelcomeMessage() { // Concrete method — has a default implementation
        return "Welcome to BookNest, " + name + "!";
    }
}
```

**Why `User` is abstract:**
- A "User" alone is meaningless — they must be either a Student or a Librarian
- The `displayMenu()` method has no meaningful default behavior at the User level
- Each subclass MUST provide its own implementation (enforced by the compiler)

---

## 6. Interfaces

**Concept:** An interface defines a contract — a set of methods that any implementing class must provide. Unlike abstract classes, interfaces focus purely on "what" without "how."

**Demonstrated in:** `BookOperations.java` (interface) → `LibraryService.java` (implementation)

**Interface definition (`BookOperations.java`):**
```java
public interface BookOperations {
    boolean addBook(Book book);
    ArrayList<Book> getAllBooks();
    ArrayList<Book> searchBooks(String keyword);
    boolean updateBook(Book book);
    boolean deleteBook(int bookId);
}
```

**Implementation (`LibraryService.java`):**
```java
public class LibraryService implements BookOperations {  // 'implements' keyword
    @Override
    public boolean addBook(Book book) {
        // Actual JDBC code to insert into database
    }
    // ... all other methods must be implemented
}
```

**Difference from abstract class:**
- Abstract class: "Here's a partial blueprint with some shared code"
- Interface: "Here's a pure contract — implement ALL these methods"

---

## 7. String Handling

**Concept:** Java's String class provides methods for searching, comparing, and validating text.

| Method | Where Used | Purpose |
|--------|-----------|---------|
| `trim()` | `Main.java`, `AuthService.java`, `LibraryService.java` | Remove leading/trailing whitespace from user input |
| `isEmpty()` | `Main.java`, `AuthService.java` | Check if a string has no characters (validation) |
| `equalsIgnoreCase()` | `Main.java` (delete confirmation), `AuthService.java` (role check) | Case-insensitive comparison ("yes" equals "YES") |
| `substring()` | `Main.java` (truncate method) | Extract part of a string for display formatting |
| `contains()` | `LibraryService.java` (error handling) | Check if error message contains "Duplicate" |
| `toLowerCase()` / SQL `LOWER()` | `LibraryService.java` | Case-insensitive book search |

**Example — Input validation in `Main.java`:**
```java
System.out.print("Enter Title: ");
String title = scanner.nextLine();
if (title.trim().isEmpty()) {              // trim() + isEmpty() for validation
    System.out.println("Error: Title cannot be empty.");
    return;
}
```

**Example — Case-insensitive search in `LibraryService.java`:**
```java
String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) OR LOWER(author) LIKE LOWER(?)";
// Searching "dune" will find "Dune", "DUNE", etc.
```

---

## 8. Collections Framework (ArrayList)

**Concept:** `ArrayList` is a resizable array from `java.util` that can grow dynamically. We use it to temporarily hold data retrieved from the database.

**Important:** MySQL is the permanent data store. ArrayList is only used for temporary in-memory display.

| Where Used | Purpose |
|-----------|---------|
| `LibraryService.getAllBooks()` | Store Book objects from SQL query for display |
| `LibraryService.searchBooks()` | Store search results |
| `LibraryService.getBookRecommendations()` | Store recommended books by genre |
| `LibraryService.getBorrowedBooks()` | Store active loan records |
| `LibraryService.getAvailableGenres()` | Store genre names |
| `Main.java` display methods | Iterate and display results |

**Key ArrayList methods used:**
```java
ArrayList<Book> books = new ArrayList<>();  // Create empty list
books.add(book);                            // Add element to end
books.isEmpty()                             // Check if list is empty
books.size()                                // Get number of elements
books.get(i)                                // Get element at index i
for (Book book : books) { ... }             // For-each loop iteration
```

**Why not other collections?**
- `HashMap`, `HashSet`, `Queue` etc. are not needed for this project
- ArrayList is sufficient for temporarily storing and displaying query results
- The database handles all permanent storage, searching, and ordering

---

## 9. JDBC (Java Database Connectivity)

**Concept:** JDBC provides a standard Java API to interact with relational databases.

**Key operations demonstrated:**

| Operation | Method | SQL |
|-----------|--------|-----|
| INSERT | `addBook()`, `registerStudent()` | `INSERT INTO books (...) VALUES (?, ?)` |
| SELECT | `getAllBooks()`, `searchBooks()` | `SELECT * FROM books WHERE ...` |
| UPDATE | `updateBook()`, `borrowBook()` | `UPDATE books SET ... WHERE ...` |
| DELETE | `deleteBook()` | `DELETE FROM books WHERE ...` |
| Transaction | `borrowBook()`, `returnBook()` | `setAutoCommit(false)`, `commit()`, `rollback()` |

**PreparedStatement prevents SQL injection:**
```java
// SAFE (parameterized query):
PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_id = ?");
stmt.setInt(1, userId);  // Parameter is safely escaped

// UNSAFE (string concatenation — NEVER do this):
// Statement stmt = conn.createStatement("SELECT * FROM users WHERE user_id = " + userId);
```

**try-with-resources for automatic cleanup:**
```java
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement stmt = conn.prepareStatement(sql)) {
    // Use conn and stmt
}  // Both are automatically closed here, even if an exception occurs
```

---

## 10. Exception Handling

**Concept:** `try-catch` blocks handle runtime errors gracefully instead of crashing the program.

| Exception | Where Handled | Scenario |
|-----------|---------------|----------|
| `SQLException` | All database methods | Database connection failures, query errors |
| `NumberFormatException` | `Main.getIntInput()` | User enters text instead of a number |
| `NoSuchAlgorithmException` | `AuthService.hashPassword()` | SHA-256 not available (extremely rare) |
| `InterruptedException` | `Main.viewLibrarySummary()` | Report thread interrupted |

**Example in `Main.java`:**
```java
private static int getIntInput() {
    try {
        String input = scanner.nextLine();
        return Integer.parseInt(input.trim());
    } catch (NumberFormatException e) {
        // User entered "abc" instead of a number — handle gracefully
        return -1;
    }
}
```

---

## 11. Multithreading

**Concept:** Running code in a separate thread so it executes concurrently with the main program.

**Demonstrated in:** `ReportGenerator.java` (extends `Thread`, overrides `run()`)

```java
public class ReportGenerator extends Thread {
    @Override
    public void run() {
        // This code runs in a SEPARATE thread
        // Generates report by querying the database
    }
}

// In Main.java:
ReportGenerator report = new ReportGenerator();
report.setName("BookNest-Report-Thread");
report.start();   // Launches a new thread
report.join();     // Main thread waits for report to finish
```

**Why multithreading here?** In a real system with a large database, report generation could take time. Running it in a separate thread allows the main application to remain responsive. For our small project, it demonstrates the concept.

---

## Summary Table

| # | OOP Concept | Primary File(s) | Key Element |
|---|------------|-----------------|-------------|
| 1 | Classes & Objects | `Book.java`, `Loan.java` | Class definitions and `new` keyword usage |
| 2 | Encapsulation | All model classes | `private` fields, `public` getters/setters |
| 3 | Inheritance | `User.java` → `Student.java`, `Librarian.java` | `extends` keyword, `super()` |
| 4 | Polymorphism | `Student.java`, `Librarian.java` | `@Override` of `displayMenu()`, `getWelcomeMessage()` |
| 5 | Abstraction | `User.java` | `abstract class`, `abstract void displayMenu()` |
| 6 | Interfaces | `BookOperations.java` → `LibraryService.java` | `interface`, `implements` |
| 7 | String Handling | `Main.java`, `AuthService.java` | `trim()`, `isEmpty()`, `equalsIgnoreCase()` |
| 8 | Collections | `LibraryService.java`, `Main.java` | `ArrayList<Book>`, `add()`, `size()`, `get()` |
| 9 | JDBC | `LibraryService.java`, `AuthService.java` | `PreparedStatement`, try-with-resources |
| 10 | Exception Handling | Throughout all files | `try-catch`, `SQLException`, `NumberFormatException` |
| 11 | Multithreading | `ReportGenerator.java` | `extends Thread`, `run()`, `start()`, `join()` |

---

## Viva Preparation — Likely Questions and Answers

### Q1: What is the difference between an abstract class and an interface?
**A:** An abstract class can have both abstract methods (no body) and concrete methods (with body), plus member variables. An interface only declares method signatures that implementing classes must provide. In our project, `User` is an abstract class with shared fields like `userId` and `name`, while `BookOperations` is an interface defining the contract for book CRUD operations.

### Q2: Why did you use an abstract class for User instead of a regular class?
**A:** Because a "User" alone is incomplete — they must be either a Student or a Librarian. Making `User` abstract prevents creating `new User(...)` directly and forces subclasses to implement `displayMenu()` with their own menu options.

### Q3: Where is polymorphism demonstrated?
**A:** In `Main.java`, the variable `user` is declared as type `User` but holds either a `Student` or `Librarian` object. When we call `user.getWelcomeMessage()`, Java automatically calls the correct overridden version based on the actual object type at runtime.

### Q4: Why use PreparedStatement instead of Statement?
**A:** PreparedStatement prevents SQL injection attacks by separating SQL code from user data. Parameters are set using `setString()` and `setInt()`, which automatically escape special characters. It's also more efficient for repeated queries.

### Q5: What is try-with-resources?
**A:** `try-with-resources` automatically closes resources (like database connections) when the try block ends, even if an exception occurs. This prevents resource leaks. Example: `try (Connection conn = ...) { ... }` — `conn` is auto-closed.

### Q6: Why use SHA-256 for passwords instead of storing them directly?
**A:** Storing plain text passwords is a security risk. SHA-256 is a one-way hash function — even if someone gets the database, they cannot reverse the hash to get the original password. During login, we hash the entered password and compare hashes.

### Q7: Why use ArrayList instead of a regular array?
**A:** We don't know in advance how many books a search query will return. ArrayList can grow dynamically, while arrays have a fixed size. ArrayList also provides convenient methods like `add()`, `size()`, and `isEmpty()`.

### Q8: Explain the transaction in the borrowBook() method.
**A:** Borrowing requires two database changes: (1) decrease `available_copies` in the books table, and (2) insert a record in the loans table. If the second operation fails, we must undo the first. A transaction ensures both succeed together (`commit()`) or both are undone (`rollback()`).

### Q9: What does `Thread.join()` do?
**A:** `join()` makes the calling thread (main thread) wait until the joined thread (report thread) finishes execution. Without it, the main menu would appear before the report is fully generated.

### Q10: What is the purpose of the BookOperations interface?
**A:** It defines a contract for book CRUD operations (add, get, search, update, delete). `LibraryService` implements this interface, guaranteeing it provides all these operations. If we later created a different service (e.g., using a file instead of a database), it would implement the same interface, ensuring consistency.
