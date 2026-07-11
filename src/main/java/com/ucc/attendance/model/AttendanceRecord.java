package com.ucc.attendance.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private Integer markedByUserId;
    private String markedByUsername;
    private LocalDateTime markedAt;

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

    public AttendanceRecord(int id, int studentId, int courseId, LocalDate attendanceDate,
                            AttendanceStatus status, String studentNumber,
                            String studentName, String courseCode,
                            Integer markedByUserId, String markedByUsername,
                            LocalDateTime markedAt) {
        this(id, studentId, courseId, attendanceDate, status, studentNumber, studentName, courseCode);
        this.markedByUserId = markedByUserId;
        this.markedByUsername = markedByUsername;
        this.markedAt = markedAt;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public Integer getMarkedByUserId() {
        return markedByUserId;
    }

    public String getMarkedByUsername() {
        return markedByUsername;
    }

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public void setMarkedByUserId(Integer markedByUserId) {
        this.markedByUserId = markedByUserId;
    }

    public void setMarkedByUsername(String markedByUsername) {
        this.markedByUsername = markedByUsername;
    }

    public void setMarkedAt(LocalDateTime markedAt) {
        this.markedAt = markedAt;
    }
}