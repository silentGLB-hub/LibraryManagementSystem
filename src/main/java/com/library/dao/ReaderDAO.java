package com.library.dao;

import com.library.config.DbUtil;
import com.library.model.Reader;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                Reader r = new Reader();
                r.setId(rs.getInt("id"));
                r.setFullName(rs.getString("full_name"));
                r.setEmail(rs.getString("email"));
                r.setPhone(rs.getString("phone"));
                r.setAddress(rs.getString("address"));
                readers.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readers;
    }

    public void save(Reader reader) throws Exception {
        if (reader.getFullName() == null || reader.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Reader name is required.");
        }
        String sql = "INSERT INTO readers(full_name,email,phone,address) VALUES(?,?,?,?)";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, reader.getFullName().trim());
            ps.setString(2, reader.getEmail());
            ps.setString(3, reader.getPhone());
            ps.setString(4, reader.getAddress());
            ps.executeUpdate();
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
                    throw new IllegalStateException("Cannot delete reader with active borrow records.");
                }
            }

            delete.setInt(1, id);
            if (delete.executeUpdate() == 0) {
                throw new IllegalStateException("Reader does not exist.");
            }
        }
    }
}
