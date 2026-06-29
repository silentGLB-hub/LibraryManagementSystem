package com.library.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbUtil {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final DatabaseSettings SETTINGS = loadSettings();

    private DbUtil() {
    }

    public static Connection getConnection() throws Exception {
        validateCloudSettings();
        Class.forName("com.mysql.cj.jdbc.Driver");
        DriverManager.setLoginTimeout(15);
        return DriverManager.getConnection(SETTINGS.url(), SETTINGS.user(), SETTINGS.password());
    }

    private static DatabaseSettings loadSettings() {
        String configuredUrl = setting("library.db.url", "LIBRARY_DB_URL", "");
        String configuredUser = setting("library.db.user", "LIBRARY_DB_USER", "");
        String configuredPassword = setting("library.db.password", "LIBRARY_DB_PASSWORD", "");

        if (!configuredUrl.isBlank()) {
            return fromUrl(configuredUrl, configuredUser, configuredPassword, true);
        }

        String providerUrl = firstEnvironment("MYSQL_PUBLIC_URL", "MYSQL_URL", "DATABASE_URL");
        if (!providerUrl.isBlank()) {
            return fromUrl(providerUrl, configuredUser, configuredPassword, true);
        }

        String host = firstEnvironment("MYSQLHOST", "MYSQL_HOST");
        if (!host.isBlank()) {
            String port = firstEnvironment("MYSQLPORT", "MYSQL_PORT");
            String database = firstEnvironment("MYSQLDATABASE", "MYSQL_DATABASE");
            String user = firstNonBlank(configuredUser, firstEnvironment("MYSQLUSER", "MYSQL_USER"));
            String password = firstNonBlank(configuredPassword,
                    firstEnvironment("MYSQLPASSWORD", "MYSQL_PASSWORD"));
            String jdbcUrl = "jdbc:mysql://" + host + ":" + defaultIfBlank(port, "3306") + "/"
                    + defaultIfBlank(database, "library_db")
                    + "?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                    + "&connectTimeout=15000&socketTimeout=30000";
            return new DatabaseSettings(jdbcUrl, user, password, true);
        }

        return new DatabaseSettings(DEFAULT_URL, "root", "", false);
    }

    private static DatabaseSettings fromUrl(String value, String user, String password,
            boolean explicitlyConfigured) {
        String trimmed = value.trim();
        if (trimmed.startsWith("jdbc:mysql://")) {
            return new DatabaseSettings(withTimeouts(trimmed), user, password, explicitlyConfigured);
        }

        if (trimmed.startsWith("mysql://")) {
            URI uri = URI.create(trimmed);
            String[] credentials = decodeCredentials(uri.getRawUserInfo());
            String database = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/", "");
            String querySeparator = uri.getRawQuery() == null ? "?" : "&";
            String jdbcUrl = "jdbc:mysql://" + uri.getHost() + ":"
                    + (uri.getPort() < 0 ? 3306 : uri.getPort()) + "/" + database;
            if (uri.getRawQuery() != null) {
                jdbcUrl += "?" + uri.getRawQuery();
            }
            jdbcUrl += querySeparator
                    + "sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                    + "&connectTimeout=15000&socketTimeout=30000";
            return new DatabaseSettings(jdbcUrl,
                    firstNonBlank(user, credentials[0]),
                    firstNonBlank(password, credentials[1]), explicitlyConfigured);
        }

        return new DatabaseSettings(trimmed, user, password, explicitlyConfigured);
    }

    private static String withTimeouts(String url) {
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + "connectTimeout=15000&socketTimeout=30000";
    }

    private static String[] decodeCredentials(String rawUserInfo) {
        if (rawUserInfo == null || rawUserInfo.isBlank()) {
            return new String[]{"", ""};
        }
        int separator = rawUserInfo.indexOf(':');
        String rawUser = separator < 0 ? rawUserInfo : rawUserInfo.substring(0, separator);
        String rawPassword = separator < 0 ? "" : rawUserInfo.substring(separator + 1);
        return new String[]{decode(rawUser), decode(rawPassword)};
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static void validateCloudSettings() throws SQLException {
        if (!isCloudDeployment()) {
            return;
        }
        if (!SETTINGS.explicitlyConfigured()) {
            throw new SQLException("Chua cau hinh MySQL cho Render. Hay thiet lap "
                    + "LIBRARY_DB_URL, LIBRARY_DB_USER va LIBRARY_DB_PASSWORD.");
        }
        String normalizedUrl = SETTINGS.url().toLowerCase();
        if (normalizedUrl.contains("localhost") || normalizedUrl.contains("127.0.0.1")) {
            throw new SQLException("LIBRARY_DB_URL dang tro toi localhost. Render phai su dung "
                    + "Public/External Host va Port cua dich vu MySQL.");
        }
        if (SETTINGS.user().isBlank()) {
            throw new SQLException("Thieu LIBRARY_DB_USER hoac tai khoan MySQL trong connection URL.");
        }
    }

    private static boolean isCloudDeployment() {
        return !firstEnvironment("RENDER", "RENDER_SERVICE_ID", "RENDER_EXTERNAL_URL").isBlank();
    }

    private static String setting(String propertyName, String environmentName, String defaultValue) {
        return firstNonBlank(System.getProperty(propertyName), System.getenv(environmentName), defaultValue);
    }

    private static String firstEnvironment(String... names) {
        for (String name : names) {
            String value = System.getenv(name);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private record DatabaseSettings(String url, String user, String password,
            boolean explicitlyConfigured) {
    }
}
