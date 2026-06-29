package com.library.dao;

import com.library.config.DbUtil;
import com.library.model.LookupItem;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CatalogDAO {

    public List<LookupItem> categories() throws Exception {
        return findAll("categories");
    }

    public List<LookupItem> authors() throws Exception {
        return findAll("authors");
    }

    public List<LookupItem> publishers() throws Exception {
        return findAll("publishers");
    }

    public void addCategory(String name) throws Exception {
        insert("categories", name);
    }

    public void addAuthor(String name) throws Exception {
        insert("authors", name);
    }

    public void addPublisher(String name) throws Exception {
        insert("publishers", name);
    }

    public void deleteCategory(int id) throws Exception {
        delete("categories", id);
    }

    public void deleteAuthor(int id) throws Exception {
        delete("authors", id);
    }

    public void deletePublisher(int id) throws Exception {
        delete("publishers", id);
    }

    private List<LookupItem> findAll(String table) throws Exception {
        List<LookupItem> items = new ArrayList<>();
        String sql = "SELECT id, name FROM " + table + " ORDER BY name";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new LookupItem(rs.getInt("id"), rs.getString("name")));
            }
        }
        return items;
    }

    private void insert(String table, String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        String sql = "INSERT INTO " + table + "(name) VALUES(?)";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.executeUpdate();
        }
    }

    private void delete(String table, int id) throws Exception {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
