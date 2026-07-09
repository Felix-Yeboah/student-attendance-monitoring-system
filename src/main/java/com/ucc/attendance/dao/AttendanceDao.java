package com.ucc.attendance.dao;

import com.ucc.attendance.database.DatabaseManager;
import com.ucc.attendance.exception.DataAccessException;
import com.ucc.attendance.model.AttendanceRecord;
import com.ucc.attendance.model.AttendanceStatus;
import com.ucc.attendance.model.DashboardStatistics;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles attendance-specific database queries, including batch saving and reporting.
 */
public class AttendanceDao {
    public List<AttendanceRecord> getRegister(int courseId, LocalDate attendanceDate) {
        String sql = """
                SELECT COALESCE(a.id, 0) AS attendance_id,
                       s.id AS student_id,
                       s.student_number,
                       CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                       ? AS course_id,
                       ? AS attendance_date,
                       COALESCE(a.status, 'PRESENT') AS status,
                       c.course_code
                FROM students s
                CROSS JOIN courses c
                LEFT JOIN attendance a ON a.student_id = s.id
                    AND a.course_id = c.id
                    AND a.attendance_date = ?
                WHERE c.id = ? AND s.active = TRUE
                ORDER BY s.student_number
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            statement.setDate(2, Date.valueOf(attendanceDate));
            statement.setDate(3, Date.valueOf(attendanceDate));
            statement.setInt(4, courseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<AttendanceRecord> records = new ArrayList<>();
                while (resultSet.next()) {
                    records.add(new AttendanceRecord(
                            resultSet.getInt("attendance_id"),
                            resultSet.getInt("student_id"),
                            resultSet.getInt("course_id"),
                            resultSet.getDate("attendance_date").toLocalDate(),
                            AttendanceStatus.valueOf(resultSet.getString("status")),
                            resultSet.getString("student_number"),
                            resultSet.getString("student_name"),
                            resultSet.getString("course_code")
                    ));
                }
                return records;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load the attendance register.", exception);
        }
    }

    public void saveAll(List<AttendanceRecord> records) {
        if (records.isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO attendance (student_id, course_id, attendance_date, status)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    status = VALUES(status),
                    marked_at = CURRENT_TIMESTAMP
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);
            for (AttendanceRecord record : records) {
                statement.setInt(1, record.getStudentId());
                statement.setInt(2, record.getCourseId());
                statement.setDate(3, Date.valueOf(record.getAttendanceDate()));
                statement.setString(4, record.getStatus().name());
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException exception) {
            throw new DataAccessException("Could not save attendance records.", exception);
        }
    }

    public List<AttendanceRecord> findRecords(Integer courseId, LocalDate attendanceDate,
                                              AttendanceStatus status) {
        StringBuilder sql = new StringBuilder("""
                SELECT a.id AS attendance_id, a.student_id, a.course_id, a.attendance_date, a.status,
                       s.student_number, CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                       c.course_code
                FROM attendance a
                INNER JOIN students s ON s.id = a.student_id
                INNER JOIN courses c ON c.id = a.course_id
                WHERE 1 = 1
                """);
        List<Object> parameters = new ArrayList<>();

        if (courseId != null) {
            sql.append(" AND a.course_id = ?");
            parameters.add(courseId);
        }
        if (attendanceDate != null) {
            sql.append(" AND a.attendance_date = ?");
            parameters.add(Date.valueOf(attendanceDate));
        }
        if (status != null) {
            sql.append(" AND a.status = ?");
            parameters.add(status.name());
        }
        sql.append(" ORDER BY a.attendance_date DESC, c.course_code, s.student_number");

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<AttendanceRecord> records = new ArrayList<>();
                while (resultSet.next()) {
                    records.add(mapRecord(resultSet));
                }
                return records;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load the attendance report.", exception);
        }
    }

    public DashboardStatistics getDashboardStatistics(LocalDate date) {
        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM students WHERE active = TRUE) AS total_students,
                    (SELECT COUNT(*) FROM courses) AS total_courses,
                    COALESCE(SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END), 0) AS present_count,
                    COALESCE(SUM(CASE WHEN status = 'LATE' THEN 1 ELSE 0 END), 0) AS late_count,
                    COALESCE(SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END), 0) AS absent_count
                FROM attendance
                WHERE attendance_date = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(date));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new DashboardStatistics(
                            resultSet.getInt("total_students"),
                            resultSet.getInt("total_courses"),
                            resultSet.getInt("present_count"),
                            resultSet.getInt("late_count"),
                            resultSet.getInt("absent_count")
                    );
                }
                return new DashboardStatistics(0, 0, 0, 0, 0);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not load dashboard statistics.", exception);
        }
    }

    private AttendanceRecord mapRecord(ResultSet resultSet) throws SQLException {
        return new AttendanceRecord(
                resultSet.getInt("attendance_id"),
                resultSet.getInt("student_id"),
                resultSet.getInt("course_id"),
                resultSet.getDate("attendance_date").toLocalDate(),
                AttendanceStatus.valueOf(resultSet.getString("status")),
                resultSet.getString("student_number"),
                resultSet.getString("student_name"),
                resultSet.getString("course_code")
        );
    }
}
