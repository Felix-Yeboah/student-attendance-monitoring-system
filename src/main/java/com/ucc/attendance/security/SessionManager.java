package com.ucc.attendance.security;

import com.ucc.attendance.model.User;

/**
 * Stores the currently logged-in user during application runtime.
 */
public final class SessionManager {
    private static User currentUser;

    private SessionManager() {
    }

    public static void login(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}