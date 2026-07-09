package com.ucc.attendance.dao;

import com.ucc.attendance.database.DatabaseManager;
import com.ucc.attendance.exception.DataAccessException;
import com.ucc.attendance.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC persistence class for Course objects.
 */
public class CourseDao implements CrudRepository<Course> {
    private static final String SELECT_BASE = """
            SELECT id, course_code, course_title, lecturer_name, semester
            FROM courses
            """;

    @Override
    public List<Course> findAll() {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BASE + " ORDER BY course_code");
             ResultSet resultSet = statement.executeQuery()) {

            List<Course> courses = new ArrayList<>();
            while (resultSet.next()) {
                courses.add(mapRow(resultSet));
            }
            return courses;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load courses.", exception);
        }
    }

    @Override
    public Optional<Course> findById(int id) {
        String sql = SELECT_BASE + " WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not find the selected course.", exception);
        }
    }

    @Override
    public Course save(Course course) {
        return course.getId() == 0 ? insert(course) : update(course);
    }

    private Course insert(Course course) {
        String sql = """
                INSERT INTO courses (course_code, course_title, lecturer_name, semester)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setCourseParameters(statement, course);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    course.setId(keys.getInt(1));
                }
            }
            return course;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not save the course. Course code must be unique.", exception);
        }
    }

    private Course update(Course course) {
        String sql = """
                UPDATE courses
                SET course_code = ?, course_title = ?, lecturer_name = ?, semester = ?
                WHERE id = ?
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setCourseParameters(statement, course);
            statement.setInt(5, course.getId());
            statement.executeUpdate();
            return course;
        } catch (SQLException exception) {
            throw new DataAccessException("Could not update the course. Course code must be unique.", exception);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Could not delete the course because attendance records may exist.", exception);
        }
    }

    private void setCourseParameters(PreparedStatement statement, Course course) throws SQLException {
        statement.setString(1, course.getCourseCode());
        statement.setString(2, course.getCourseTitle());
        statement.setString(3, course.getLecturerName());
        statement.setString(4, course.getSemester());
    }

    private Course mapRow(ResultSet resultSet) throws SQLException {
        return new Course(
                resultSet.getInt("id"),
                resultSet.getString("course_code"),
                resultSet.getString("course_title"),
                resultSet.getString("lecturer_name"),
                resultSet.getString("semester")
        );
    }
}
