package com.ucc.attendance.model;

/**
 * A student is a specialised Person. It inherits common identity data and adds
 * student-specific information such as a student number and programme.
 */
public class Student extends Person {
    private int id;
    private String studentNumber;
    private String programme;
    private String gender;
    private boolean active = true;

    public Student() {
        super();
    }

    public Student(int id, String studentNumber, String firstName, String lastName,
                   String email, String programme, String gender, boolean active) {
        super(firstName, lastName, email);
        this.id = id;
        this.studentNumber = studentNumber;
        this.programme = programme;
        this.gender = gender;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getRole() {
        return "Student";
    }
}
