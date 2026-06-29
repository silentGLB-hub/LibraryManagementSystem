CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS borrow_records;
DROP TABLE IF EXISTS readers;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS publishers;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(120),
  phone VARCHAR(30),
  role ENUM('ADMIN','LIBRARIAN','READER') NOT NULL DEFAULT 'READER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  info_url VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE authors (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  info_url VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE publishers (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  info_url VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE books (
  id INT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(30) UNIQUE NOT NULL,
  title VARCHAR(200) NOT NULL,
  category_id INT NULL,
  author_id INT NULL,
  publisher_id INT NULL,
  quantity INT NOT NULL DEFAULT 0,
  available INT NOT NULL DEFAULT 0,
  cover_image VARCHAR(255),
  preview_text LONGTEXT,
  content_text LONGTEXT,
  chapters TEXT,
  pdf_file VARCHAR(500),
  CONSTRAINT chk_books_quantity CHECK (quantity >= 0),
  CONSTRAINT chk_books_available CHECK (available >= 0 AND available <= quantity),
  CONSTRAINT fk_books_category FOREIGN KEY (category_id) REFERENCES categories(id) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES authors(id) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE readers (
  id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(120),
  phone VARCHAR(20),
  address VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE borrow_records (
  id INT AUTO_INCREMENT PRIMARY KEY,
  book_id INT NOT NULL,
  reader_id INT NOT NULL,
  borrow_date DATE NOT NULL,
  due_date DATE NOT NULL,
  return_date DATE NULL,
  status ENUM('BORROWING','RETURNED','OVERDUE') NOT NULL DEFAULT 'BORROWING',
  fine DECIMAL(12,2) NOT NULL DEFAULT 0,
  CONSTRAINT chk_borrow_dates CHECK (due_date >= borrow_date),
  CONSTRAINT chk_return_dates CHECK (return_date IS NULL OR return_date >= borrow_date),
  CONSTRAINT chk_borrow_fine CHECK (fine >= 0),
  CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES books(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_borrow_reader FOREIGN KEY (reader_id) REFERENCES readers(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users(username,password,full_name,email,phone,role) VALUES
('admin','123','System Admin','admin@library.local','0900000000','ADMIN'),
('lib','123','Librarian','lib@library.local','0900000003','LIBRARIAN'),
('reader','123','Reader Demo','reader@library.local','0900000004','READER');

INSERT INTO categories(name,info_url) VALUES
('Computer Science','https://en.wikipedia.org/wiki/Computer_science'),
('Novel','https://en.wikipedia.org/wiki/Novel'),
('Business','https://en.wikipedia.org/wiki/Business');

INSERT INTO authors(name,info_url) VALUES
('Robert Martin','https://en.wikipedia.org/wiki/Robert_C._Martin'),
('Nguyen Nhat Anh','https://vi.wikipedia.org/wiki/Nguy%E1%BB%85n_Nh%E1%BA%ADt_%C3%81nh'),
('Dale Carnegie','https://en.wikipedia.org/wiki/Dale_Carnegie');

INSERT INTO publishers(name,info_url) VALUES
('Education','https://en.wikipedia.org/wiki/Education'),
('Youth','https://en.wikipedia.org/wiki/Youth'),
('General','https://en.wikipedia.org/wiki/Publishing');

INSERT INTO books(code,title,category_id,author_id,publisher_id,quantity,available,cover_image,preview_text,content_text,chapters,pdf_file) VALUES
('B001','Clean Code',1,1,1,10,10,'/assets/img/book1.svg','Preview page 1: Clean Code introduces practical habits for writing readable software.
Preview page 2: The book focuses on naming, functions, classes, and disciplined design.','Chapter 1: Clean Code
Chapter 2: Meaningful Names
Chapter 3: Functions
Chapter 4: Classes','Chapter 1: Clean Code
This demo content is available only to readers with an active borrow record.

Chapter 2: Meaningful Names
Use clear names that reveal intent and reduce confusion.',NULL),
('B002','Toi thay hoa vang tren co xanh',2,2,2,8,8,'/assets/img/book2.svg','Preview page 1: A gentle story of childhood, memory, and countryside life.
Preview page 2: The preview introduces the tone and main characters.','De muc 1: Tuoi tho
De muc 2: Tinh ban
De muc 3: Ky uc','Noi dung doc thu nghiem cho sach. Reader can view this after borrowing successfully.',NULL),
('B003','How to Win Friends',3,3,3,12,12,'/assets/img/book3.svg','Preview page 1: Principles for improving communication and relationships.
Preview page 2: The book highlights empathy, respect, and listening.','Part 1: Fundamental Techniques
Part 2: Ways to Make People Like You
Part 3: Win People to Your Way of Thinking','Protected reading content for borrowed readers. This sample represents the full book area.',NULL);

INSERT INTO readers(full_name,email,phone,address) VALUES
('Nguyen Van A','a@gmail.com','0900000001','HCM'),
('Tran Thi B','b@gmail.com','0900000002','Ha Noi');
