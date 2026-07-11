package com.ucc.attendance.dao;

import com.ucc.attendance.database.DatabaseManager;
import com.ucc.attendance.exception.DataAccessException;
import com.ucc.attendance.model.User;
import com.ucc.attendance.model.UserRole;
import com.ucc.attendance.security.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Handles database operations for application users.
 */
public class UserDao {

    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT id, full_name, username, password_salt, password_hash, role, active
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Could not load user account.", exception);
        }
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> optionalUser = findByUsername(username);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        if (!user.isActive()) {
            return Optional.empty();
        }

        boolean validPassword = PasswordUtil.verifyPassword(
                password,
                user.getPasswordSalt(),
                user.getPasswordHash()
        );

        return validPassword ? Optional.of(user) : Optional.empty();
    }

    public void ensureDefaultUsers() {
        String countSql = "SELECT COUNT(*) FROM users";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(countSql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next() && resultSet.getInt(1) == 0) {
                createDefaultUser(
                        connection,
                        "System Administrator",
                        "admin",
                        "Admin@2026",
                        UserRole.ADMIN
                );

                createDefaultUser(
                        connection,
                        "Example Lecturer",
                        "lecturer",
                        "Lecturer@2026",
                        UserRole.LECTURER
                );
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Could not prepare default user accounts.", exception);
        }
    }

    public boolean resetPassword(String username, String currentPassword, String newPassword) {
        Optional<User> optionalUser = authenticate(username, currentPassword);

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        String newSalt = PasswordUtil.generateSalt();
        String newHash = PasswordUtil.hashPassword(newPassword, newSalt);

        String sql = """
                UPDATE users
                SET password_salt = ?, password_hash = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newSalt);
            statement.setString(2, newHash);
            statement.setInt(3, user.getId());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            throw new DataAccessException("Could not reset user password.", exception);
        }
    }

    private void createDefaultUser(Connection connection, String fullName, String username,
                                   String plainPassword, UserRole role) throws SQLException {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(plainPassword, salt);

        String sql = """
                INSERT INTO users (full_name, username, password_salt, password_hash, role, active)
                VALUES (?, ?, ?, ?, ?, TRUE)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fullName);
            statement.setString(2, username);
            statement.setString(3, salt);
            statement.setString(4, hash);
            statement.setString(5, role.name());
            statement.executeUpdate();
        }
    }

    private User mapRow(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("full_name"),
                resultSet.getString("username"),
                resultSet.getString("password_salt"),
                resultSet.getString("password_hash"),
                UserRole.valueOf(resultSet.getString("role")),
                resultSet.getBoolean("active")
        );
    }
}