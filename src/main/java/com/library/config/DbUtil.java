package com.library.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtil {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String URL = setting("library.db.url", "LIBRARY_DB_URL", DEFAULT_URL);
    private static final String USER = setting("library.db.user", "LIBRARY_DB_USER", "root");
    private static final String PASSWORD = setting("library.db.password", "LIBRARY_DB_PASSWORD", "");

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String setting(String propertyName, String environmentName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }

        return defaultValue;
    }
}
