package com.ucc.attendance.model;

/**
 * Small immutable value object used by the dashboard rather than passing unrelated numbers around.
 */
public class DashboardStatistics {
    private final int totalStudents;
    private final int totalCourses;
    private final int todayPresent;
    private final int todayLate;
    private final int todayAbsent;

    public DashboardStatistics(int totalStudents, int totalCourses, int todayPresent,
                               int todayLate, int todayAbsent) {
        this.totalStudents = totalStudents;
        this.totalCourses = totalCourses;
        this.todayPresent = todayPresent;
        this.todayLate = todayLate;
        this.todayAbsent = todayAbsent;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public int getTodayPresent() {
        return todayPresent;
    }

    public int getTodayLate() {
        return todayLate;
    }

    public int getTodayAbsent() {
        return todayAbsent;
    }
}
