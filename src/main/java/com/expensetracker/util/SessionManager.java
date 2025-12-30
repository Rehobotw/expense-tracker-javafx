package com.expensetracker.util;

import com.expensetracker.model.User;

/**
 * Manages the current logged-in user session.
 * Central source of truth for user-based data isolation.
 */
public final class SessionManager {

    private static User currentUser;

    private SessionManager() {}

    /* ============================
       SESSION MANAGEMENT
       ============================ */

    /**
     * Set the logged-in user after successful authentication.
     */
    public static void setUser(User user) {
        currentUser = user;
    }

    /**
     * Get the currently logged-in user.
     */
    public static User getUser() {
        return currentUser;
    }

    /**
     * Get the logged-in user's ID.
     * @throws IllegalStateException if no user is logged in
     */
    public static int getUserId() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        return currentUser.getId();
    }

    /**
     * Check if a user is currently logged in.
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Clear session on logout.
     */
    public static void logout() {
        currentUser = null;
    }
}
