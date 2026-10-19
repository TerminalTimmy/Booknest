# BookNest — Library Management System

A console-based Library Management System built with Java and MySQL for a 3rd-semester OOP course project.

---

## Features

### Student Features
- Login with student ID and password
- View all available books
- Search books by title or author (case-insensitive)
- Borrow available books
- Return borrowed books
- View currently borrowed books
- View full borrowing history
- Reserve unavailable books
- View personal notifications
- **Book Match** — genre-based book recommendations

### Librarian Features
- Login with librarian credentials
- Add, view, search, update, and delete books
- Register new students
- View all borrowing records
- View overdue books
- View library summary report (generated using multithreading)

---

## Prerequisites

| Software | Version | Check Command |
|----------|---------|---------------|
| Java JDK | 8 or higher | `java -version` |
| MySQL Server | 5.7 or higher | `mysql --version` |
| MySQL JDBC Connector | Any compatible `.jar` | See setup below |

---

## Project Structure

```
BookNest/
├── src/
│   ├── Main.java              # Entry point, console menus
│   ├── User.java              # Abstract base class (Abstraction, Inheritance)
│   ├── Student.java           # Student class (Inheritance, Polymorphism)
│   ├── Librarian.java         # Librarian class (Inheritance, Polymorphism)
│   ├── Book.java              # Book model (Encapsulation)
│   ├── Loan.java              # Loan model (Encapsulation)
│   ├── BookOperations.java    # Interface (Interface concept)
│   ├── LibraryService.java    # Core logic (Implements BookOperations)
│   ├── AuthService.java       # Authentication & password hashing
│   ├── DatabaseConnection.java # JDBC connection utility
│   └── ReportGenerator.java   # Multithreading demonstration
├── database/
│   ├── schema.sql             # Database table definitions
│   └── sample_data.sql        # Sample test data
├── README.md                  # This file
├── OOP_CONCEPTS.md            # Where each OOP concept is used
└── AI_PROMPT_REPORT.md        # AI prompt documentation template
```

---

## Setup Instructions

### Step 1: Configure the MySQL Database

Open a terminal/command prompt and run:

```bash
# Log in to MySQL
mysql -u root -p

# Inside the MySQL prompt, run the schema file:
SOURCE C:/Users/tuhin/Music/Java_projects/Booknest/database/schema.sql;

# Then load sample data:
SOURCE C:/Users/tuhin/Music/Java_projects/Booknest/database/sample_data.sql;

# Verify:
USE booknest_db;
SELECT * FROM users;
SELECT * FROM books;
EXIT;
```

Alternatively, in MySQL Workbench: `File → Open SQL Script` → run `schema.sql` then `sample_data.sql`.

### Step 2: Configure Database Password

Edit `src/DatabaseConnection.java` (line 24) and set your MySQL root password:

```java
private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";
```

### Step 3: Download the MySQL JDBC Connector

If you don't have it, download `mysql-connector-j-X.X.X.jar` from:
https://dev.mysql.com/downloads/connector/j/

Note the path where the JAR is saved (e.g., `C:\Users\tuhin\Downloads\mysql-connector-j-26.7.0.jar`).

### Step 4: Compile

```bash
cd C:\Users\tuhin\Music\Java_projects\Booknest

javac -cp ".;C:\Users\tuhin\Downloads\mysql-connector-j-26.7.0.jar" -d bin src\*.java
```

> **Note:** On macOS/Linux, replace `;` with `:` in the classpath separator.

### Step 5: Run

```bash
java -cp "bin;C:\Users\tuhin\Downloads\mysql-connector-j-26.7.0.jar" Main
```

---

## Default Login Credentials

| Role | User ID | Password | Name |
|------|---------|----------|------|
| Librarian | 1 | admin123 | Admin Librarian |
| Student | 2 | rahul123 | Rahul Sharma |
| Student | 3 | priya123 | Priya Patel |
| Student | 4 | amit123 | Amit Kumar |

---

## Password Security

Passwords are **never stored in plain text**. The application uses SHA-256 hashing:

1. When a user enters a password, it is hashed using `MessageDigest.getInstance("SHA-256")`
2. The resulting hash (a 64-character hex string) is stored in the database
3. During login, the entered password is hashed and compared against the stored hash
4. Even if the database is compromised, actual passwords cannot be recovered from hashes

---

## Sample Workflows

### Borrowing a Book
1. Login as Student (ID: 2, Password: rahul123)
2. Select option 3 (Borrow a Book)
3. View the book list and enter a Book ID
4. The system checks availability and creates a loan with a 14-day due date

### Reserving an Unavailable Book
1. Login as Student (ID: 4, Password: amit123)
2. Select option 7 (Reserve an Unavailable Book)
3. Enter the Book ID of a book with 0 available copies
4. When the book is returned by another student, you'll receive a notification

### Book Match Recommendations
1. Login as any Student
2. Select option 9 (Book Match)
3. Choose a genre from the list
4. View recommended available books in that genre

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Cannot connect to database" | Check MySQL is running, database exists, and password in `DatabaseConnection.java` is correct |
| "ClassNotFoundException" | Ensure the MySQL JDBC JAR is in the classpath during compilation and execution |
| Compilation errors | Verify Java JDK is installed: `javac -version` |
| "Access denied" | Check MySQL username and password in `DatabaseConnection.java` |
| Data not appearing | Run `sample_data.sql` after `schema.sql` |

---

## Technology Stack

- **Language:** Java (JDK 8+)
- **Database:** MySQL 8.0
- **Connectivity:** JDBC with PreparedStatement
- **Build:** Manual compilation with `javac` (no Maven/Gradle required)
- **Interface:** Console-based (Scanner for input)
- **Collections:** ArrayList (for temporary data display)
- **Security:** SHA-256 password hashing
