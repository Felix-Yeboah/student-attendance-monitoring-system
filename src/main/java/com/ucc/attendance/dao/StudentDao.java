package com.ucc.attendance.dao;

import com.ucc.attendance.database.DatabaseManager;
import com.ucc.attendance.exception.DataAccessException;
import com.ucc.attendance.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of student persistence. PreparedStatement prevents SQL injection
 * and keeps user values separate from SQL commands.
 */
public class StudentDao implements CrudRepository<Student> {
    private static final String SELECT_BASE = """
            SELECT id, student_number, first_name, last_name, email, programme, gender, active
            FROM students
            """;

    @Override
    public List<Student> findAll() {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BASE + " ORDER BY student_number");
             ResultSet resultSet = statement.executeQuery()) {

            List<Student> students = new ArrayList<>();
            while (resultSet.next()) {
                students.add(mapRow(resultSet));
            }
            return students;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load students.", exception);
        }
    }

    public List<Student> search(String searchText) {
        String sql = """
                SELECT id, student_number, first_name, last_name, email, programme, gender, active
                FROM students
                WHERE student_number LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR programme LIKE ?
                ORDER BY student_number
                """;
        String searchPattern = "%" + searchText.trim() + "%";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int index = 1; index <= 4; index++) {
                statement.setString(index, searchPattern);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Student> students = new ArrayList<>();
                while (resultSet.next()) {
                    students.add(mapRow(resultSet));
                }
                return students;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not search students.", exception);
        }
    }

    @Override
    public Optional<Student> findById(int id) {
        String sql = SELECT_BASE + " WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not find the selected student.", exception);
        }
    }

    @Override
    public Student save(Student student) {
        return student.getId() == 0 ? insert(student) : update(student);
    }

    private Student insert(Student student) {
        String sql = """
                INSERT INTO students (student_number, first_name, last_name, email, programme, gender, active)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setStudentParameters(statement, student);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    student.setId(keys.getInt(1));
                }
            }
            return student;
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Could not save the student. Student number and email must be unique.", exception);
        }
    }

    private Student update(Student student) {
        String sql = """
                UPDATE students
                SET student_number = ?, first_name = ?, last_name = ?, email = ?, programme = ?, gender = ?, active = ?
                WHERE id = ?
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setStudentParameters(statement, student);
            statement.setInt(8, student.getId());
            statement.executeUpdate();
            return student;
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Could not update the student. Student number and email must be unique.", exception);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Could not delete the student because attendance records may exist.", exception);
        }
    }

    private void setStudentParameters(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getStudentNumber());
        statement.setString(2, student.getFirstName());
        statement.setString(3, student.getLastName());
        statement.setString(4, student.getEmail());
        statement.setString(5, student.getProgramme());
        statement.setString(6, student.getGender());
        statement.setBoolean(7, student.isActive());
    }

    private Student mapRow(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getInt("id"),
                resultSet.getString("student_number"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("email"),
                resultSet.getString("programme"),
                resultSet.getString("gender"),
                resultSet.getBoolean("active")
        );
    }
}
