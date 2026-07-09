package com.ucc.attendance.util;

import com.ucc.attendance.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Reusable validation rules. Keeping validation here makes controller methods shorter and consistent.
 */
public final class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern STUDENT_NUMBER_PATTERN = Pattern.compile("^[A-Za-z0-9/-]{4,20}$");
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Za-z]{2,8}[0-9]{2,4}[A-Za-z]?$");

    private InputValidator() {
        // Utility class: do not instantiate.
    }

    public static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required.");
        }
        return value.trim();
    }

    public static String requireEmail(String value) {
        String email = requireText(value, "Email address");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Enter a valid email address, for example student@ucc.edu.gh.");
        }
        return email;
    }

    public static String requireStudentNumber(String value) {
        String studentNumber = requireText(value, "Student number").toUpperCase();
        if (!STUDENT_NUMBER_PATTERN.matcher(studentNumber).matches()) {
            throw new ValidationException("Student number must contain 4-20 letters, numbers, slashes, or hyphens.");
        }
        return studentNumber;
    }

    public static String requireCourseCode(String value) {
        String courseCode = requireText(value, "Course code").toUpperCase();
        if (!COURSE_CODE_PATTERN.matcher(courseCode).matches()) {
            throw new ValidationException("Course code should look like INF811D or CSC201.");
        }
        return courseCode;
    }
}
