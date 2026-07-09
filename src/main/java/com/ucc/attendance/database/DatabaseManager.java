package com.ucc.attendance.database;

import com.ucc.attendance.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralises JDBC connection creation so DAO classes do not repeat connection configuration.
 */
public final class DatabaseManager {
    private DatabaseManager() {
        // Utility class: do not instantiate.
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    DatabaseConfig.getUrl(),
                    DatabaseConfig.getUsername(),
                    DatabaseConfig.getPassword()
            );
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Unable to connect to MySQL. Check the database service and credentials.",
                    exception
            );
        }
    }
}
