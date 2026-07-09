package com.ucc.attendance.database;

import com.ucc.attendance.exception.DataAccessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the tables and inserts safe demonstration data on first application launch.
 */
public final class DatabaseInitializer {
    private DatabaseInitializer() {
        // Utility class: do not instantiate.
    }

    public static void initialize() {
        executeSqlScript("db/schema.sql");
        executeSqlScript("db/seed.sql");
    }

    private static void executeSqlScript(String resourcePath) {
        String script = readScript(resourcePath);

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            for (String command : script.split(";")) {
                String sql = command.trim();
                if (!sql.isBlank()) {
                    statement.execute(sql);
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not initialise database tables.", exception);
        }
    }

    private static String readScript(String resourcePath) {
        InputStream stream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalStateException("Missing SQL script: " + resourcePath);
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!trimmedLine.startsWith("--") && !trimmedLine.isBlank()) {
                    builder.append(line).append('\n');
                }
            }
            return builder.toString();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read SQL script: " + resourcePath, exception);
        }
    }
}
