package com.ucc.attendance.model;

import java.time.LocalDate;

/**
 * Represents one student's attendance status for one course on one date.
 */
public class AttendanceRecord {
    private int id;
    private int studentId;
    private int courseId;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private String studentNumber;
    private String studentName;
    private String courseCode;

    public AttendanceRecord() {
        // Default constructor.
    }

    public AttendanceRecord(int id, int studentId, int courseId, LocalDate attendanceDate,
                            AttendanceStatus status, String studentNumber,
                            String studentName, String courseCode) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.courseCode = courseCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
