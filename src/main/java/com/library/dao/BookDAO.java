package com.library.dao;

import com.library.config.DbUtil;
import com.library.model.Book;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookDAO {

    public List<Book> findAll() throws Exception {
        return search(null);
    }

    public List<Book> search(String keyword) throws Exception {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT b.id, b.code, b.title, a.name AS author, a.info_url AS author_url, "
                + "c.name AS category, c.info_url AS category_url, p.name AS publisher, p.info_url AS publisher_url, "
                + "b.quantity, b.available, b.cover_image, b.preview_text, b.content_text, b.chapters, b.pdf_file "
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

        try (Connection c = DbUtil.getConnection()) {
            ensureSchema(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                if (hasKeyword) {
                    String like = "%" + keyword.trim().toLowerCase() + "%";
                    for (int i = 1; i <= 5; i++) {
                        ps.setString(i, like);
                    }
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(map(rs));
                    }
                }
            }
        }

        return list;
    }

    public Book findById(int id) throws Exception {
        String sql = "SELECT b.id, b.code, b.title, a.name AS author, a.info_url AS author_url, "
                + "c.name AS category, c.info_url AS category_url, p.name AS publisher, p.info_url AS publisher_url, "
                + "b.quantity, b.available, b.cover_image, b.preview_text, b.content_text, b.chapters, b.pdf_file "
                + "FROM books b "
                + "LEFT JOIN authors a ON b.author_id = a.id "
                + "LEFT JOIN categories c ON b.category_id = c.id "
                + "LEFT JOIN publishers p ON b.publisher_id = p.id "
                + "WHERE b.id = ?";
        try (Connection c = DbUtil.getConnection()) {
            ensureSchema(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return map(rs);
                    }
                }
            }
        }
        return null;
    }

    public void save(Book book) throws Exception {
        save(book, 0, 0, 0);
    }

    public void save(Book book, int categoryId, int authorId, int publisherId) throws Exception {
        String sql = "INSERT INTO books(code,title,category_id,author_id,publisher_id,quantity,available,cover_image,preview_text,content_text,chapters,pdf_file) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DbUtil.getConnection()) {
            ensureSchema(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, book.getCode().trim());
                ps.setString(2, book.getTitle().trim());
                setNullableId(ps, 3, categoryId);
                setNullableId(ps, 4, authorId);
                setNullableId(ps, 5, publisherId);
                ps.setInt(6, book.getQuantity());
                ps.setInt(7, book.getQuantity());
                ps.setString(8, normalizeCoverImage(book.getCoverImage()));
                ps.setString(9, normalize(book.getPreviewText()));
                ps.setString(10, normalize(book.getContentText()));
                ps.setString(11, normalize(book.getChapters()));
                ps.setString(12, normalize(book.getPdfFile()));
                ps.executeUpdate();
            }
        }
    }

    public void updateCoverImage(int id, String coverImage) throws Exception {
        String sql = "UPDATE books SET cover_image = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection()) {
            ensureSchema(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, normalizeCoverImage(coverImage));
                ps.setInt(2, id);
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException("Sách không tồn tại.");
                }
            }
        }
    }

    public void updateReadingContent(int id, String previewText, String contentText, String chapters) throws Exception {
        String sql = "UPDATE books SET preview_text = ?, content_text = ?, chapters = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection()) {
            ensureSchema(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, normalize(previewText));
                ps.setString(2, normalize(contentText));
                ps.setString(3, normalize(chapters));
                ps.setInt(4, id);
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException("Sách không tồn tại.");
                }
            }
        }
    }


    public void updatePdfFile(int id, String pdfFile) throws Exception {
        String sql = "UPDATE books SET pdf_file = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection()) {
            ensureSchema(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, normalize(pdfFile));
                ps.setInt(2, id);
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException("Sách không tồn tại.");
                }
            }
        }
    }
    public void delete(int id) throws Exception {
        try (Connection c = DbUtil.getConnection()) {
            ensureSchema(c);
            try (PreparedStatement active = c.prepareStatement(
                     "SELECT COUNT(*) FROM borrow_records WHERE book_id = ? AND status <> 'RETURNED'");
                 PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id = ?")) {

                active.setInt(1, id);
                try (ResultSet rs = active.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalStateException("Không thể xóa sách đang được mượn.");
                    }
                }

                ps.setInt(1, id);
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException("Sách không tồn tại.");
                }
            }
        }
    }

    private Book map(ResultSet rs) throws Exception {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setCode(rs.getString("code"));
        b.setTitle(rs.getString("title"));
        b.setCategory(rs.getString("category"));
        b.setAuthor(rs.getString("author"));
        b.setPublisher(rs.getString("publisher"));
        b.setCategoryUrl(rs.getString("category_url"));
        b.setAuthorUrl(rs.getString("author_url"));
        b.setPublisherUrl(rs.getString("publisher_url"));
        b.setQuantity(rs.getInt("quantity"));
        b.setAvailable(rs.getInt("available"));
        b.setCoverImage(rs.getString("cover_image"));
        b.setPreviewText(rs.getString("preview_text"));
        b.setContentText(rs.getString("content_text"));
        b.setChapters(rs.getString("chapters"));
        b.setPdfFile(rs.getString("pdf_file"));
        return b;
    }

    private void ensureSchema(Connection c) throws Exception {
        ensureColumn(c, "books", "preview_text", "LONGTEXT NULL");
        ensureColumn(c, "books", "content_text", "LONGTEXT NULL");
        ensureColumn(c, "books", "chapters", "TEXT NULL");
        ensureColumn(c, "books", "pdf_file", "VARCHAR(500) NULL");
        ensureColumn(c, "authors", "info_url", "VARCHAR(500) NULL");
        ensureColumn(c, "categories", "info_url", "VARCHAR(500) NULL");
        ensureColumn(c, "publishers", "info_url", "VARCHAR(500) NULL");
    }

    private void ensureColumn(Connection c, String table, String column, String definition) throws Exception {
        if (!columnExists(c, table, column)) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
            }
        }
    }

    private boolean columnExists(Connection c, String table, String columnName) throws Exception {
        try (ResultSet rs = c.getMetaData().getColumns(c.getCatalog(), null, table, columnName)) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = c.getMetaData().getColumns(c.getCatalog(), null, table.toUpperCase(), columnName.toUpperCase())) {
            return rs.next();
        }
    }

    private String normalizeCoverImage(String coverImage) {
        if (coverImage == null || coverImage.trim().isEmpty()) {
            return "/assets/img/book1.svg";
        }
        return coverImage.trim();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void setNullableId(PreparedStatement ps, int index, int value) throws Exception {
        if (value <= 0) {
            ps.setObject(index, null);
        } else {
            ps.setInt(index, value);
        }
    }
}