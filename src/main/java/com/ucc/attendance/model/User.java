package com.ucc.attendance.model;

/**
 * Represents a user who can log into the system.
 */
public class User {
    private int id;
    private String fullName;
    private String username;
    private String passwordSalt;
    private String passwordHash;
    private UserRole role;
    private boolean active;

    public User(int id, String fullName, String username, String passwordSalt,
                String passwordHash, UserRole role, boolean active) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.passwordSalt = passwordSalt;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }
}