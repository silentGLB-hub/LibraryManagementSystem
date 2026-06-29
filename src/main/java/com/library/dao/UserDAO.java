package com.library.dao;

import com.library.config.DbUtil;
import com.library.model.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAO {

    public User login(String username, String password) throws Exception {
        String sql = "SELECT id, username, password, full_name, role, email, phone FROM users WHERE username = ? AND password = ?";
        try (Connection c = DbUtil.getConnection()) {
            ensureContactColumns(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, username.trim());
                ps.setString(2, password.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return map(rs);
                    }
                }
            }
        }
        return null;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, full_name, role, email, phone FROM users ORDER BY role, username";
        try (Connection c = DbUtil.getConnection()) {
            ensureContactColumns(c);
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(map(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public void register(String username, String password, String fullName) throws Exception {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập và mật khẩu là bắt buộc.");
        }
        String displayName = fullName == null || fullName.trim().isEmpty() ? username.trim() : fullName.trim();
        String email = username.trim();
        String sql = "INSERT INTO users(username,password,full_name,email,phone,role) VALUES(?,?,?,?,NULL,'READER')";
        try (Connection c = DbUtil.getConnection()) {
            ensureContactColumns(c);
            try (PreparedStatement ps = c.prepareStatement(sql);
                 PreparedStatement reader = c.prepareStatement("INSERT INTO readers(full_name,email,phone,address) VALUES(?,?,NULL,NULL)")) {
                ps.setString(1, username.trim());
                ps.setString(2, password.trim());
                ps.setString(3, displayName);
                ps.setString(4, email);
                ps.executeUpdate();

                reader.setString(1, displayName);
                reader.setString(2, email);
                reader.executeUpdate();
            }
        }
    }

    public void updateRole(int id, String role) throws Exception {
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Vai trò không hợp lệ.");
        }
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalStateException("Người dùng không tồn tại.");
            }
        }
    }

    public void updateContact(int id, String email, String phone) throws Exception {
        String cleanEmail = normalize(email);
        String cleanPhone = normalize(phone);
        try (Connection c = DbUtil.getConnection()) {
            ensureContactColumns(c);
            try (PreparedStatement ps = c.prepareStatement("UPDATE users SET email = ?, phone = ? WHERE id = ?")) {
                ps.setString(1, cleanEmail);
                ps.setString(2, cleanPhone);
                ps.setInt(3, id);
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException("Người dùng không tồn tại.");
                }
            }
            syncReaderContactByUserId(c, id, cleanEmail, cleanPhone);
        }
    }

    public void updateContactForUsername(String username, String email, String phone) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        try (Connection c = DbUtil.getConnection()) {
            ensureContactColumns(c);
            try (PreparedStatement ps = c.prepareStatement("UPDATE users SET email = ?, phone = ? WHERE username = ?")) {
                ps.setString(1, normalize(email));
                ps.setString(2, normalize(phone));
                ps.setString(3, username.trim());
                ps.executeUpdate();
            }
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalStateException("Người dùng không tồn tại.");
            }
        }
    }

    private void syncReaderContactByUserId(Connection c, int userId, String email, String phone) throws Exception {
        String sql = "UPDATE readers r JOIN users u "
                + "ON (LOWER(r.full_name) = LOWER(u.full_name) OR LOWER(r.full_name) = LOWER(u.username) OR LOWER(r.email) = LOWER(u.username)) "
                + "SET r.email = ?, r.phone = ? WHERE u.id = ? AND u.role = 'READER'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    private void ensureContactColumns(Connection c) throws Exception {
        if (!columnExists(c, "email")) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("ALTER TABLE users ADD COLUMN email VARCHAR(120) NULL");
            }
        }
        if (!columnExists(c, "phone")) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("ALTER TABLE users ADD COLUMN phone VARCHAR(30) NULL");
            }
        }
    }

    private boolean columnExists(Connection c, String columnName) throws Exception {
        try (ResultSet rs = c.getMetaData().getColumns(c.getCatalog(), null, "users", columnName)) {
            if (rs.next()) {
                return true;
            }
        }
        try (ResultSet rs = c.getMetaData().getColumns(c.getCatalog(), null, "USERS", columnName.toUpperCase())) {
            return rs.next();
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isValidRole(String role) {
        return "ADMIN".equals(role) || "LIBRARIAN".equals(role) || "READER".equals(role);
    }

    private User map(ResultSet rs) throws Exception {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(rs.getString("role"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        return u;
    }
}