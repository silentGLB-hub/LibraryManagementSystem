package com.library.dao;

import com.library.config.DbUtil;
import com.library.model.Book;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookDAO {

    public List<Book> findAll() throws Exception {
        return search(null);
    }

    public List<Book> search(String keyword) throws Exception {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT b.id, b.code, b.title, a.name AS author, "
                + "c.name AS category, p.name AS publisher, b.quantity, b.available, b.cover_image "
                + "FROM books b "
                + "LEFT JOIN authors a ON b.author_id = a.id "
                + "LEFT JOIN categories c ON b.category_id = c.id "
                + "LEFT JOIN publishers p ON b.publisher_id = p.id ";

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql += "WHERE LOWER(b.code) LIKE ? OR LOWER(b.title) LIKE ? "
                    + "OR LOWER(a.name) LIKE ? OR LOWER(c.name) LIKE ? OR LOWER(p.name) LIKE ? ";
        }
        sql += "ORDER BY b.id DESC";

        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (hasKeyword) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                for (int i = 1; i <= 5; i++) {
                    ps.setString(i, like);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt("id"));
                    b.setCode(rs.getString("code"));
                    b.setTitle(rs.getString("title"));
                    b.setCategory(rs.getString("category"));
                    b.setAuthor(rs.getString("author"));
                    b.setPublisher(rs.getString("publisher"));
                    b.setQuantity(rs.getInt("quantity"));
                    b.setAvailable(rs.getInt("available"));
                    b.setCoverImage(rs.getString("cover_image"));
                    list.add(b);
                }
            }
        }

        return list;
    }

    public void save(Book book) throws Exception {
        save(book, 0, 0, 0);
    }

    public void save(Book book, int categoryId, int authorId, int publisherId) throws Exception {
        String sql = "INSERT INTO books(code,title,category_id,author_id,publisher_id,quantity,available,cover_image) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, book.getCode().trim());
            ps.setString(2, book.getTitle().trim());
            setNullableId(ps, 3, categoryId);
            setNullableId(ps, 4, authorId);
            setNullableId(ps, 5, publisherId);
            ps.setInt(6, book.getQuantity());
            ps.setInt(7, book.getQuantity());
            ps.setString(8, normalizeCoverImage(book.getCoverImage()));
            ps.executeUpdate();
        }
    }

    public void updateCoverImage(int id, String coverImage) throws Exception {
        String sql = "UPDATE books SET cover_image = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, normalizeCoverImage(coverImage));
            ps.setInt(2, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalStateException("Book does not exist.");
            }
        }
    }

    public void delete(int id) throws Exception {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement active = c.prepareStatement(
                     "SELECT COUNT(*) FROM borrow_records WHERE book_id = ? AND status <> 'RETURNED'");
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id = ?")) {

            active.setInt(1, id);
            try (ResultSet rs = active.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalStateException("Cannot delete a borrowed book.");
                }
            }

            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalStateException("Book does not exist.");
            }
        }
    }

    private String normalizeCoverImage(String coverImage) {
        if (coverImage == null || coverImage.trim().isEmpty()) {
            return "/assets/img/book1.svg";
        }
        return coverImage.trim();
    }

    private void setNullableId(PreparedStatement ps, int index, int value) throws Exception {
        if (value <= 0) {
            ps.setObject(index, null);
        } else {
            ps.setInt(index, value);
        }
    }
}
