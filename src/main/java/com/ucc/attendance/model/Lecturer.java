package com.ucc.attendance.model;

/**
 * Another Person subtype. It is included to make the inherited role model explicit
 * and provides a clear contrast with Student for OOP discussion.
 */
public class Lecturer extends Person {
    private String staffNumber;

    public Lecturer(String staffNumber, String firstName, String lastName, String email) {
        super(firstName, lastName, email);
        this.staffNumber = staffNumber;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    @Override
    public String getRole() {
        return "Lecturer";
    }
}
