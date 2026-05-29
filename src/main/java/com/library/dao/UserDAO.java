package com.library.dao;

import com.library.config.DbUtil;
import com.library.model.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class UserDAO {

    public User login(String username, String password) throws Exception {
        String sql = "SELECT id, username, password, full_name, role FROM users WHERE username = ? AND password = ?";

        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setFullName(rs.getString("full_name"));
                    u.setRole(rs.getString("role"));
                    return u;
                }
            }
        }

        return null;
    }

    public void register(String username, String password, String fullName) throws Exception {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        String sql = "INSERT INTO users(username,password,full_name,role) VALUES(?,?,?,'READER')";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            ps.setString(3, fullName == null || fullName.trim().isEmpty() ? username.trim() : fullName.trim());
            ps.executeUpdate();
        }
    }
}
