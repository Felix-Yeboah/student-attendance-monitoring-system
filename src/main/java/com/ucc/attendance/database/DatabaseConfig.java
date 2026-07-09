package com.ucc.attendance.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Reads database settings. Environment variables take priority so credentials can be
 * supplied securely during deployment. For local development, database.properties is used.
 */
public final class DatabaseConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        Path localConfig = Path.of("database.properties");
        try (InputStream input = Files.exists(localConfig)
                ? Files.newInputStream(localConfig)
                : DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read database.properties.", exception);
        }
    }

    private DatabaseConfig() {
        // Utility class: do not instantiate.
    }

    public static String getUrl() {
        return getValue(
                "DB_URL",
                "db.url",
                "jdbc:mysql://localhost:3306/student_attendance_db?createDatabaseIfNotExist=true"
                        + "&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        );
    }

    public static String getUsername() {
        return getValue("DB_USERNAME", "db.username", "root");
    }

    public static String getPassword() {
        return getValue("DB_PASSWORD", "db.password", "");
    }

    private static String getValue(String environmentKey, String propertyKey, String defaultValue) {
        String environmentValue = System.getenv(environmentKey);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        return PROPERTIES.getProperty(propertyKey, defaultValue).trim();
    }
}
