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
  role ENUM('ADMIN','LIBRARIAN','READER') NOT NULL DEFAULT 'READER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE authors (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE publishers (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL
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

INSERT INTO users(username,password,full_name,role) VALUES
('admin','123','System Admin','ADMIN'),
('lib','123','Librarian','LIBRARIAN'),
('reader','123','Reader Demo','READER');

INSERT INTO categories(name) VALUES
('Computer Science'),
('Novel'),
('Business');

INSERT INTO authors(name) VALUES
('Robert Martin'),
('Nguyen Nhat Anh'),
('Dale Carnegie');

INSERT INTO publishers(name) VALUES
('Education'),
('Youth'),
('General');

INSERT INTO books(code,title,category_id,author_id,publisher_id,quantity,available,cover_image) VALUES
('B001','Clean Code',1,1,1,10,10,'/assets/img/book1.svg'),
('B002','Toi thay hoa vang tren co xanh',2,2,2,8,8,'/assets/img/book2.svg'),
('B003','How to Win Friends',3,3,3,12,12,'/assets/img/book3.svg');

INSERT INTO readers(full_name,email,phone,address) VALUES
('Nguyen Van A','a@gmail.com','0900000001','HCM'),
('Tran Thi B','b@gmail.com','0900000002','Ha Noi');
