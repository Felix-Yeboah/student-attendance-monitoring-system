package com.ucc.attendance.service;

import com.ucc.attendance.dao.AttendanceDao;
import com.ucc.attendance.model.AttendanceRecord;
import com.ucc.attendance.model.AttendanceStatus;
import com.ucc.attendance.model.Course;
import com.ucc.attendance.model.DashboardStatistics;

import java.time.LocalDate;
import java.util.List;

/**
 * Business layer between the JavaFX controller and attendance database queries.
 */
public class AttendanceService {
    private final AttendanceDao attendanceDao = new AttendanceDao();

    public List<AttendanceRecord> loadRegister(Course course, LocalDate attendanceDate) {
        return attendanceDao.getRegister(course.getId(), attendanceDate);
    }

    public void saveAttendance(List<AttendanceRecord> records) {
        attendanceDao.saveAll(records);
    }

    public List<AttendanceRecord> getReport(Integer courseId, LocalDate attendanceDate,
                                            AttendanceStatus status) {
        return attendanceDao.findRecords(courseId, attendanceDate, status);
    }

    public DashboardStatistics getDashboardStatistics(LocalDate date) {
        return attendanceDao.getDashboardStatistics(date);
    }
}
