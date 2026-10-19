# 12. AI Usage Log

Each student must maintain an **AI Usage Log**.

For significant interactions with ChatGPT or another LLM, record:

| Date | Task | AI Tool | Prompt/Question | What Was Used | Student Verification |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 11/10/26 | Project Scoping & Constraints | ChatGPT | "How to design a college Java project meeting OOP requirements without JavaFX/Swing or heavy collection frameworks?" | Architectural guidance & recommendations on console menu design | Verified with course syllabus that console-based menu and basic `ArrayList` fulfill 3rd-semester criteria. |
| 11/10/26 | Interface Contract Definition | Claude AI | "What methods should be declared in a `BookOperations` interface to keep library CRUD operations clean and decoupled?" | Interface method signatures (`addBook`, `getAllBooks`, `searchBooks`, `updateBook`, `deleteBook`) | Implemented in `BookOperations.java`; checked that compiler requires `LibraryService` to override all contract methods. |
| 12/10/26 | User Inheritance Hierarchy | Claude AI | "How to structure an abstract `User` class inherited by `Student` and `Librarian` with constructor chaining and polymorphic menus?" | Class hierarchy skeleton using `extends`, `super()`, and abstract `displayMenu()` | Compiled `User.java`, `Student.java`, and `Librarian.java`; confirmed `user.getWelcomeMessage()` triggers student or librarian behavior dynamically. |
| 12/10/26 | Model Classes & Encapsulation | Gemini AI | "How should the `Book` and `Loan` entity classes be structured to demonstrate encapsulation and constructor overloading?" | Field declarations, private access modifiers, overloaded constructors, and getters/setters | Inspected `Book.java` and `Loan.java`; ensured all member variables are `private` with no direct public access. |
| 13/10/26 | Database Schema & Constraints | Claude AI | "Write MySQL DDL for books, users, loans, reservations, and notifications with foreign keys and unique constraints." | Table schemas in `schema.sql` | Executed script in MySQL; verified relational integrity with foreign keys referencing `users(user_id)` and `books(book_id)`. |
| 13/10/26 | Test Dataset Generation | Gemini AI | "Generate realistic sample data for 12 books across 5 genres, sample users, overdue loans, and active reservations using MySQL date math." | SQL statements in `sample_data.sql` utilizing `DATE_SUB()` and `CURDATE()` | Ran queries in MySQL workbench to verify that available copy counts properly matched active checkouts. |
| 14/10/26 | Centralized Database Connection | ChatGPT | "How to write a reusable `DatabaseConnection` class using JDBC DriverManager and try-with-resources?" | Connection utility method with exception handling and driver registration | Tested `DatabaseConnection.testConnection()`; successfully established a link to local `booknest_db` on localhost:3306. |
| 14/10/26 | Password Security & Hashing | ChatGPT | "How to implement SHA-256 password hashing in standard Java using `java.security.MessageDigest`?" | Hashing logic converting byte array to hexadecimal string | Compared Java output against MySQL's `SHA2('admin123', 256)`; verified exact hash match and tested login authentication. |
| 15/10/26 | Stock Checking & Book Checkout | Gemini AI | "Write JDBC code to safely borrow a book: check available copies, prevent duplicate active borrowings by the same student, and decrement stock." | Multi-step SQL query logic in `borrowBook()` | Attempted to borrow a book with 0 available copies and verified the application gracefully denied checkout and suggested reservation. |
| 15/10/26 | FIFO Reservation Logic | Claude AI | "How to query active book reservations in chronological FIFO order without using Java Queue collections?" | SQL query ordered by `reservation_date ASC, reservation_id ASC LIMIT 1` | Inserted two test reservations for the same book; confirmed that the earlier reservation was selected first upon return. |
| 15/10/26 | JDBC Database Transactions | Claude AI | "How to wrap book return, copy increment, reservation fulfillment, and notification insertion in a single atomic JDBC transaction?" | Transaction pattern using `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()` | Tested book return with an active reservation; verified that returning the book simultaneously generated a user notification. |
| 16/10/26 | Case-Insensitive String Search | Gemini AI | "How to perform case-insensitive partial search on book titles and authors using SQL `LIKE` and Java `trim()`?" | Parameterized SQL query: `LOWER(title) LIKE LOWER(?)` with `%keyword%` | Tested searches with 'clean', 'CLEAN', 'code', and 'martin'; verified all matched Robert C. Martin's *Clean Code*. |
| 16/10/26 | Book Match Recommendation | ChatGPT | "How to implement a simple, explainable genre-based recommendation feature using basic SQL and `ArrayList`?" | SQL filtering by genre and available copies, returning `ArrayList<Book>` | Selected genre 'Technology' in student menu; confirmed only available books in that genre appeared with the recommendation explanation. |
| 17/10/26 | Overdue Detection & Date Math | Gemini AI | "How to calculate overdue days in Java using `java.time.LocalDate` and `ChronoUnit.DAYS` for active loans?" | Date calculation snippet: `ChronoUnit.DAYS.between(dueDate, LocalDate.now())` | Tested librarian option 8; confirmed overdue loans displayed the correct number of elapsed days past the due date. |
| 17/10/26 | Multithreaded Summary Report | Claude AI | "How to create a simple background thread in Java extending `Thread` to compute library aggregate counts without blocking the user?" | Background thread class `ReportGenerator` overriding `run()` and using `join()` | Ran option 9 in librarian menu; verified thread execution logs showing `BookNest-Report-Thread` executing in parallel. |
| 18/10/26 | Scanner Input Validation | ChatGPT | "How to prevent infinite loops and `InputMismatchException` in `Scanner` when users type text instead of numbers?" | Safe wrapper method `getIntInput()` with `Integer.parseInt(scanner.nextLine().trim())` and `try-catch` | Entered invalid inputs ('abc', '!@#', empty enters) at various menu prompts; confirmed app handles errors without crashing. |
| 18/10/26 | Console Table Formatting | Claude AI | "How to format console tables cleanly with column padding and text truncation in standard Java without external libraries?" | `String.format("%-5s %-35s ...")` layout and `truncate()` helper | Checked output of 'View All Books'; verified long book titles like *Introduction to Algorithms* fit neatly within table boundaries. |
| 18/10/26 | Build & Classpath Verification | Gemini AI | "How to compile and execute a multi-file Java project with an external MySQL JDBC connector JAR using Windows PowerShell?" | Command-line `javac` and `java` commands using `-cp` flag | Executed compilation and launch from terminal; verified application connects to database and runs properly from clean build. |

---

### Purpose of AI Usage
The purpose is **not to penalize AI usage**. Instead, students are expected to demonstrate responsible, transparent AI-assisted software development by understanding, adapting, verifying, and testing all suggested code.

---

### Detailed Log Entries & Verification Records

#### Entry 1: Project Architecture & Interface Decoupling (11/10/26)
- **Prompt:** *"What methods should be declared in a `BookOperations` interface to keep library CRUD operations clean and decoupled?"*
- **What Was Used:** Method definitions for `addBook`, `getAllBooks`, `searchBooks`, `updateBook`, and `deleteBook`.
- **Student Verification:** Created `BookOperations.java`. Verified that `LibraryService implements BookOperations` enforces these methods at compile time, demonstrating Java's Interface concept.

#### Entry 2: User Hierarchy & Polymorphism (12/10/26)
- **Prompt:** *"How to structure an abstract `User` class inherited by `Student` and `Librarian` with constructor chaining and polymorphic menus?"*
- **What Was Used:** Base class `User` with abstract `displayMenu()` and polymorphic `getWelcomeMessage()`, inherited by `Student` and `Librarian` using `super()`.
- **Student Verification:** Tested dynamic method dispatch in `Main.java`:
  ```java
  User user = AuthService.login(userId, password);
  System.out.println(user.getWelcomeMessage()); // Calls Student or Librarian version at runtime
  ```

#### Entry 3: Relational Schema & MySQL Date Math (13/10/26)
- **Prompt:** *"Write MySQL DDL for books, users, loans, reservations, and notifications with foreign keys, and generate realistic sample data with relative dates."*
- **What Was Used:** DDL scripts with `DATE_SUB(CURDATE(), INTERVAL n DAY)` for testing overdue books and active loans.
- **Student Verification:** Loaded scripts into MySQL CLI and checked table structure:
  ```bash
  mysql -u root -p < database/schema.sql
  mysql -u root -p booknest_db < database/sample_data.sql
  ```
  Confirmed 12 books, 4 users, and 5 loan records created correctly.

#### Entry 4: Security via SHA-256 Password Hashing (14/10/26)
- **Prompt:** *"How to implement SHA-256 password hashing in standard Java using `java.security.MessageDigest`?"*
- **What Was Used:** `AuthService.hashPassword()` function to convert raw passwords into 64-character hexadecimal hashes.
- **Student Verification:** Verified password security by checking that the database contains only hex strings (e.g., `240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9` for `admin123`) rather than plaintext.

#### Entry 5: Transactional Integrity for Borrow & Return (15/10/26)
- **Prompt:** *"How to wrap book return, copy increment, reservation fulfillment, and notification insertion in a single atomic JDBC transaction?"*
- **What Was Used:** JDBC transaction handling pattern with explicit `commit()` and `rollback()` calls.
- **Student Verification:** Tested returning a book ('Gone Girl') that had an active reservation by Amit. Checked MySQL database:
  - `loans.return_date` was set to current date.
  - `books.available_copies` was updated.
  - `reservations.status` changed from `ACTIVE` to `FULFILLED`.
  - A new record appeared in `notifications` for user ID 4 (Amit).

#### Entry 6: Case-Insensitive Search & String Handling (16/10/26)
- **Prompt:** *"How to perform case-insensitive partial search on book titles and authors using SQL `LIKE` and Java `trim()`?"*
- **What Was Used:** String handling functions `trim()`, `isEmpty()`, and SQL `LOWER()` function with `%` wildcards.
- **Student Verification:** Searched for `"dune"`, `"DUNE"`, and `"  dune  "`. In all three cases, Frank Herbert's *Dune* was returned accurately.

#### Entry 7: Multithreaded Background Report Generation (17/10/26)
- **Prompt:** *"How to create a simple background thread in Java extending `Thread` to compute library aggregate counts without blocking the user?"*
- **What Was Used:** `ReportGenerator` class extending `java.lang.Thread`, overriding `run()`, and synchronized with `join()`.
- **Student Verification:** Verified from console outputs that the worker thread (`BookNest-Report-Thread`) computes counts in parallel and prints the completed summary.

#### Entry 8: Input Validation & Crash Prevention (18/10/26)
- **Prompt:** *"How to prevent infinite loops and `InputMismatchException` in `Scanner` when users type text instead of numbers?"*
- **What Was Used:** `getIntInput()` utility using `Integer.parseInt(scanner.nextLine().trim())` wrapped in a `try-catch` block.
- **Student Verification:** Repeatedly typed alphabetic strings, symbols, and blank lines into integer input prompts; verified that menu displayed "Invalid choice" instead of crashing with unhandled exceptions.

---

### Student Declaration & Compliance with AI Guidelines

Students must **not**:
- Claim that AI-generated code is entirely their own work.
- Submit code that they cannot explain.
- Blindly copy an AI-generated solution without testing it.
- Use AI to fabricate test results.
- Submit fabricated screenshots.
- Ask AI to generate a false project log or false experimental observations.

**Declaration:** All AI interactions for BookNest were critically evaluated, adapted to course constraints (no Swing, no complex collections, plain JDBC), and experimentally tested in the local development environment.
