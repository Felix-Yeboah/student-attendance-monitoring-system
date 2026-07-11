package com.ucc.attendance.model;

/**
 * Represents an academic course/module in the attendance system.
 */
public class Course {

    private int id;
    private String courseCode;
    private String courseTitle;
    private String lecturerName;
    private String lecturerUsername;
    private String semester;
    private Integer lecturerUserId;

    public Course() {
        // Default constructor required when creating a new Course object.
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


    public String getLecturerUsername() {
        return lecturerUsername;
    }

    public void setLecturerUsername(String lecturerUsername) {
        this.lecturerUsername = lecturerUsername;
    }


    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }


    public Integer getLecturerUserId() {
        return lecturerUserId;
    }

    public void setLecturerUserId(Integer lecturerUserId) {
        this.lecturerUserId = lecturerUserId;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseTitle;
    }
}