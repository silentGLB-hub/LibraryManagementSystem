package com.library.dao;

import com.library.config.DbUtil;
import com.library.model.Reader;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReaderDAO {

    public List<Reader> findAll() {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT id, full_name, email, phone, address FROM readers ORDER BY id DESC";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                readers.add(map(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readers;
    }

    public void save(Reader reader) throws Exception {
        if (reader.getFullName() == null || reader.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên độc giả là bắt buộc.");
        }
        String sql = "INSERT INTO readers(full_name,email,phone,address) VALUES(?,?,?,?)";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, reader.getFullName().trim());
            ps.setString(2, normalize(reader.getEmail()));
            ps.setString(3, normalize(reader.getPhone()));
            ps.setString(4, normalize(reader.getAddress()));
            ps.executeUpdate();
        }
    }

    public Reader findOrCreateForAccount(String username, String fullName, String email, String phone) throws Exception {
        Reader reader = findForAccount(username, fullName);
        if (reader != null) {
            return reader;
        }
        String displayName = normalize(fullName);
        if (displayName == null) {
            displayName = normalize(username);
        }
        if (displayName == null) {
            throw new IllegalArgumentException("Tài khoản độc giả thiếu tên hiển thị.");
        }
        String contactEmail = normalize(email);
        if (contactEmail == null) {
            contactEmail = normalize(username);
        }
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO readers(full_name,email,phone,address) VALUES(?,?,?,NULL)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, displayName);
            ps.setString(2, contactEmail);
            ps.setString(3, normalize(phone));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    Reader created = new Reader();
                    created.setId(keys.getInt(1));
                    created.setFullName(displayName);
                    created.setEmail(contactEmail);
                    created.setPhone(normalize(phone));
                    return created;
                }
            }
        }
        return findForAccount(username, fullName);
    }

    public void updateContact(int id, String email, String phone) throws Exception {
        String sql = "UPDATE readers SET email = ?, phone = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, normalize(email));
            ps.setString(2, normalize(phone));
            ps.setInt(3, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalStateException("Hồ sơ độc giả không tồn tại.");
            }
        }
    }

    public void delete(int id) throws Exception {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement active = c.prepareStatement(
                     "SELECT COUNT(*) FROM borrow_records WHERE reader_id = ? AND status <> 'RETURNED'");
             PreparedStatement delete = c.prepareStatement("DELETE FROM readers WHERE id = ?")) {

            active.setInt(1, id);
            try (ResultSet rs = active.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalStateException("Không thể xóa độc giả đang có phiếu mượn hoạt động.");
                }
            }

            delete.setInt(1, id);
            if (delete.executeUpdate() == 0) {
                throw new IllegalStateException("Độc giả không tồn tại.");
            }
        }
    }

    public Reader findForAccount(String username, String fullName) {
        String sql = "SELECT id, full_name, email, phone, address FROM readers "
                + "WHERE LOWER(email) = LOWER(?) OR LOWER(full_name) = LOWER(?) OR LOWER(full_name) = LOWER(?) "
                + "ORDER BY id DESC LIMIT 1";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username == null ? "" : username.trim());
            ps.setString(2, fullName == null ? "" : fullName.trim());
            ps.setString(3, username == null ? "" : username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Reader map(ResultSet rs) throws Exception {
        Reader r = new Reader();
        r.setId(rs.getInt("id"));
        r.setFullName(rs.getString("full_name"));
        r.setEmail(rs.getString("email"));
        r.setPhone(rs.getString("phone"));
        r.setAddress(rs.getString("address"));
        return r;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}