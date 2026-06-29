package com.library.dao;

import com.library.config.DbUtil;
import com.library.model.LookupItem;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
        insert("categories", name, null);
    }

    public void addCategory(String name, String infoUrl) throws Exception {
        insert("categories", name, infoUrl);
    }

    public void addAuthor(String name) throws Exception {
        insert("authors", name, null);
    }

    public void addAuthor(String name, String infoUrl) throws Exception {
        insert("authors", name, infoUrl);
    }

    public void addPublisher(String name) throws Exception {
        insert("publishers", name, null);
    }

    public void addPublisher(String name, String infoUrl) throws Exception {
        insert("publishers", name, infoUrl);
    }

    public void updateCategory(int id, String name, String infoUrl) throws Exception {
        update("categories", id, name, infoUrl);
    }

    public void updateAuthor(int id, String name, String infoUrl) throws Exception {
        update("authors", id, name, infoUrl);
    }

    public void updatePublisher(int id, String name, String infoUrl) throws Exception {
        update("publishers", id, name, infoUrl);
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
        try (Connection c = DbUtil.getConnection()) {
            ensureInfoUrlColumn(c, table);
            String sql = "SELECT id, name, info_url FROM " + table + " ORDER BY name";
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new LookupItem(rs.getInt("id"), rs.getString("name"), rs.getString("info_url")));
                }
            }
        }
        return items;
    }

    private void insert(String table, String name, String infoUrl) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên là bắt buộc.");
        }
        try (Connection c = DbUtil.getConnection()) {
            ensureInfoUrlColumn(c, table);
            String sql = "INSERT INTO " + table + "(name, info_url) VALUES(?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name.trim());
                ps.setString(2, normalize(infoUrl));
                ps.executeUpdate();
            }
        }
    }

    private void update(String table, int id, String name, String infoUrl) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên là bắt buộc.");
        }
        try (Connection c = DbUtil.getConnection()) {
            ensureInfoUrlColumn(c, table);
            String sql = "UPDATE " + table + " SET name = ?, info_url = ? WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name.trim());
                ps.setString(2, normalize(infoUrl));
                ps.setInt(3, id);
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException("Mục danh mục không tồn tại.");
                }
            }
        }
    }

    private void delete(String table, int id) throws Exception {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void ensureInfoUrlColumn(Connection c, String table) throws Exception {
        if (!columnExists(c, table, "info_url")) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("ALTER TABLE " + table + " ADD COLUMN info_url VARCHAR(500) NULL");
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

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}