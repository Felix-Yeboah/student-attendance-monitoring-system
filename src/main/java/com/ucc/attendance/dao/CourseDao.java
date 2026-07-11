package com.ucc.attendance.dao;

import com.ucc.attendance.database.DatabaseManager;
import com.ucc.attendance.exception.DataAccessException;
import com.ucc.attendance.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    public List<Course> findAll() {
        String sql = """
                SELECT c.id,
                       c.course_code,
                       c.course_title,
                       c.lecturer_name,
                       c.semester,
                       c.lecturer_user_id,
                       u.username AS lecturer_username
                FROM courses c
                LEFT JOIN users u ON c.lecturer_user_id = u.id
                ORDER BY c.course_code
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
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

    public List<Course> findByLecturerUserId(int lecturerUserId) {
        String sql = """
                SELECT c.id,
                       c.course_code,
                       c.course_title,
                       c.lecturer_name,
                       c.semester,
                       c.lecturer_user_id,
                       u.username AS lecturer_username
                FROM courses c
                LEFT JOIN users u ON c.lecturer_user_id = u.id
                WHERE c.lecturer_user_id = ?
                ORDER BY c.course_code
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, lecturerUserId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Course> courses = new ArrayList<>();

                while (resultSet.next()) {
                    courses.add(mapRow(resultSet));
                }

                return courses;
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Could not load lecturer courses.", exception);
        }
    }

    public void save(Course course) {
        if (course.getId() == 0) {
            insert(course);
        } else {
            update(course);
        }
    }

    private void insert(Course course) {
        String sql = """
                INSERT INTO courses (
                    course_code,
                    course_title,
                    lecturer_name,
                    semester,
                    lecturer_user_id
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setCourseParameters(statement, course);
            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new DataAccessException("Could not create course.", exception);
        }
    }

    private void update(Course course) {
        String sql = """
                UPDATE courses
                SET course_code = ?,
                    course_title = ?,
                    lecturer_name = ?,
                    semester = ?,
                    lecturer_user_id = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setCourseParameters(statement, course);
            statement.setInt(6, course.getId());
            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new DataAccessException("Could not update course.", exception);
        }
    }

    private void setCourseParameters(PreparedStatement statement, Course course) throws SQLException {
        statement.setString(1, course.getCourseCode());
        statement.setString(2, course.getCourseTitle());
        statement.setString(3, course.getLecturerName());
        statement.setString(4, course.getSemester());

        if (course.getLecturerUserId() == null) {
            statement.setNull(5, java.sql.Types.INTEGER);
        } else {
            statement.setInt(5, course.getLecturerUserId());
        }
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new DataAccessException("Could not delete course.", exception);
        }
    }

    private Course mapRow(ResultSet resultSet) throws SQLException {
        Course course = new Course();

        course.setId(resultSet.getInt("id"));
        course.setCourseCode(resultSet.getString("course_code"));
        course.setCourseTitle(resultSet.getString("course_title"));
        course.setLecturerName(resultSet.getString("lecturer_name"));
        course.setSemester(resultSet.getString("semester"));

        int lecturerUserId = resultSet.getInt("lecturer_user_id");

        if (resultSet.wasNull()) {
            course.setLecturerUserId(null);
        } else {
            course.setLecturerUserId(lecturerUserId);
        }

        course.setLecturerUsername(resultSet.getString("lecturer_username"));

        return course;
    }
}