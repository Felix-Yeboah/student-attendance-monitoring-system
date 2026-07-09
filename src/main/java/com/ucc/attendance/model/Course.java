package com.ucc.attendance.model;

/**
 * Represents a university course that can be selected for attendance recording.
 */
public class Course {
    private int id;
    private String courseCode;
    private String courseTitle;
    private String lecturerName;
    private String semester;

    public Course() {
        // Default constructor for form-based creation.
    }

    public Course(int id, String courseCode, String courseTitle, String lecturerName, String semester) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.lecturerName = lecturerName;
        this.semester = semester;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseTitle;
    }
}
