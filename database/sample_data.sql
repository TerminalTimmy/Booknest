-- Sample data for BookNest. Run after schema.sql
-- Default passwords: admin123, rahul123, priya123, amit123

USE booknest_db;

-- ----------------------------------------------------------------------------
-- Sample Users
-- ----------------------------------------------------------------------------
-- 1 Librarian and 3 Students (passwords hashed with SHA-256)
INSERT INTO users (user_id, name, password_hash, role) VALUES
(1, 'Admin Librarian', SHA2('admin123', 256), 'LIBRARIAN'),
(2, 'Rahul Sharma',    SHA2('rahul123', 256), 'STUDENT'),
(3, 'Priya Patel',     SHA2('priya123', 256), 'STUDENT'),
(4, 'Amit Kumar',      SHA2('amit123', 256),  'STUDENT');

-- ----------------------------------------------------------------------------
-- Sample Books
-- ----------------------------------------------------------------------------
-- 12 books across Technology, Mystery, Science Fiction, Literature, and History.
-- Note: available_copies values account for the active loans inserted below.
INSERT INTO books (book_id, title, author, genre, isbn, total_copies, available_copies) VALUES
(1,  'Introduction to Algorithms',      'Thomas Cormen',      'Technology',      '978-0262033848', 3, 3),
(2,  'Clean Code',                      'Robert C. Martin',   'Technology',      '978-0132350884', 2, 1),
(3,  'The Pragmatic Programmer',        'David Thomas',       'Technology',      '978-0135957059', 2, 2),
(4,  'The Girl with the Dragon Tattoo', 'Stieg Larsson',      'Mystery',         '978-0307949486', 2, 2),
(5,  'Gone Girl',                       'Gillian Flynn',      'Mystery',         '978-0307588371', 1, 0),
(6,  'Dune',                            'Frank Herbert',      'Science Fiction', '978-0441013593', 3, 2),
(7,  'Neuromancer',                     'William Gibson',     'Science Fiction', '978-0441569595', 1, 1),
(8,  'Foundation',                      'Isaac Asimov',       'Science Fiction', '978-0553293357', 2, 2),
(9,  'To Kill a Mockingbird',           'Harper Lee',         'Literature',      '978-0061120084', 2, 1),
(10, 'Pride and Prejudice',             'Jane Austen',        'Literature',      '978-0141439518', 3, 3),
(11, 'Sapiens',                         'Yuval Noah Harari',  'History',         '978-0062316097', 2, 2),
(12, '1984',                            'George Orwell',      'Literature',      '978-0451524935', 2, 2);

-- ----------------------------------------------------------------------------
-- Sample Loans
-- ----------------------------------------------------------------------------
-- Loans reflect active checkouts, one overdue loan, and one returned loan.
INSERT INTO loans (loan_id, user_id, book_id, borrow_date, due_date, return_date) VALUES
-- Rahul borrowed 'Clean Code' 10 days ago, due in 4 days, not returned
(1, 2, 2, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), NULL),

-- Priya borrowed 'Dune' 5 days ago, due in 9 days, not returned
(2, 3, 6, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 9 DAY), NULL),

-- Rahul borrowed 'To Kill a Mockingbird' 30 days ago, due 16 days ago (OVERDUE!), not returned
(3, 2, 9, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 16 DAY), NULL),

-- Amit borrowed and returned 'Foundation' (borrowed 20 days ago, due 6 days ago, returned 3 days ago)
(4, 4, 8, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY)),

-- Priya borrowed 'Gone Girl' 3 days ago, due in 11 days, not returned (available copies becomes 0)
(5, 3, 5, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 11 DAY), NULL);

-- ----------------------------------------------------------------------------
-- Sample Reservations
-- ----------------------------------------------------------------------------
-- Amit has an ACTIVE reservation for 'Gone Girl' (all copies currently borrowed)
INSERT INTO reservations (reservation_id, user_id, book_id, reservation_date, status) VALUES
(1, 4, 5, DATE_SUB(NOW(), INTERVAL 2 DAY), 'ACTIVE');

-- ----------------------------------------------------------------------------
-- Sample Notifications
-- ----------------------------------------------------------------------------
-- Welcome notification for Rahul (marked as read)
INSERT INTO notifications (notification_id, user_id, message, created_at, is_read) VALUES
(1, 2, 'Welcome to BookNest!', DATE_SUB(NOW(), INTERVAL 10 DAY), TRUE);
