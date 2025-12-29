package com.expensetracker.dao;

import com.expensetracker.dao.Database;
import com.expensetracker.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    /**
     * Authenticate a user with email and password.
     *
     * @param name    User email
     * @param password User password
     * @return User object if authenticated, null otherwise
     */
    public User authenticate(String name, String password) {
        String sql = "SELECT * FROM users WHERE full_name=? AND password=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                return new User(id, fullName, name, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Register a new user.
     *
     * @param user User object to insert
     * @return true if registration successful, false otherwise
     */
    public boolean register(User user) {
        String sql = "INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if email is already registered.
     *
     * @param email User email
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
