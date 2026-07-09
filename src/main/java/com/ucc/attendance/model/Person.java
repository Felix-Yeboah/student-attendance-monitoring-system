package com.ucc.attendance.model;

/**
 * Abstract parent for people in the attendance domain. It demonstrates abstraction:
 * every person has names and an email, but each role describes itself differently.
 */
public abstract class Person {
    private String firstName;
    private String lastName;
    private String email;

    protected Person() {
        // Required for subclasses and form construction.
    }

    protected Person(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public abstract String getRole();
}
