package com.ucc.attendance.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttendanceStatusTest {
    @Test
    void presentStatusHasFriendlyDisplayName() {
        assertEquals("Present", AttendanceStatus.PRESENT.getDisplayName());
    }
}
