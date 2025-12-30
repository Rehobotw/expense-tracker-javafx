package com.expensetracker.service;

import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;

/**
 * Service layer for user-related business logic.
 */
public class UserService {

    private final UserDAO dao = new UserDAO();

    /**
     * Authenticate a user.
     *
     * @param name   User name
     * @param password User password
     * @return User object if authenticated, null otherwise
     */
    public User login(String name, String password) {
        return dao.authenticate(name, password);
    }

    /**
     * Register a new user.
     *
     * @param user User object containing full name, email, and password
     * @return true if registration successful, false otherwise
     */
    public boolean register(User user) {
        if (dao.emailExists(user.getEmail())) {
            // Email already exists
            return false;
        }
        return dao.register(user);
    }

    /**
     * Check if a user email is already registered.
     *
     * @param email User email
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        return dao.emailExists(email);
    }
}
