-- ============================================================================
-- BookNest Library Management System
-- Database Schema Definition
-- ============================================================================

CREATE DATABASE IF NOT EXISTS booknest_db;
USE booknest_db;

-- Drop existing tables in reverse dependency order for a clean setup
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- ----------------------------------------------------------------------------
-- Table: users
-- Description: Stores user accounts including librarians and students with
--              SHA-256 hashed passwords and designated roles.
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,  -- stores SHA-256 hash
    role ENUM('STUDENT', 'LIBRARIAN') NOT NULL
);

-- ----------------------------------------------------------------------------
-- Table: books
-- Description: Stores book catalog details including inventory counts for
--              total copies and currently available copies.
-- ----------------------------------------------------------------------------
CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1
);

-- ----------------------------------------------------------------------------
-- Table: loans
-- Description: Tracks book borrowings by users, due dates, and return dates.
--              A NULL return_date indicates the book is currently borrowed.
-- ----------------------------------------------------------------------------
CREATE TABLE loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,  -- nullable: null means still borrowed
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

-- ----------------------------------------------------------------------------
-- Table: reservations
-- Description: Manages book reservations when copies are unavailable.
--              Status indicates whether reservation is active, fulfilled, or cancelled.
-- ----------------------------------------------------------------------------
CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    reservation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'FULFILLED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    UNIQUE KEY unique_active_reservation (user_id, book_id, status)  -- prevents duplicate active reservations with application-level logic
);

-- ----------------------------------------------------------------------------
-- Table: notifications
-- Description: Stores system notifications for users (e.g., availability alerts,
--              overdue reminders, welcome messages).
-- ----------------------------------------------------------------------------
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message VARCHAR(500) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
