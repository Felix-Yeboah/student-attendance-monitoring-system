package com.ucc.attendance.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentTest {
    @Test
    void studentInheritsFullNameFromPerson() {
        Student student = new Student(1, "UCC/IT/006", "Akosua", "Agyeman",
                "akosua@example.edu.gh", "MSc Information Technology", "Female", true);

        assertEquals("Akosua Agyeman", student.getFullName());
        assertEquals("Student", student.getRole());
    }
}
