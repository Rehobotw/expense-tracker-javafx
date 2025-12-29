package com.expensetracker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class Database {
    private static final String URL = "jdbc:sqlite:expense_tracker.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {

            s.execute("PRAGMA foreign_keys = ON;");

            // Create Users table - using full_name to match your UI and Model
            s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
            """);

            // MIGRATION: Ensure columns exist if the database was created with an old version
            ensureColumnExists(c, "users", "full_name", "TEXT NOT NULL DEFAULT ''");
            ensureColumnExists(c, "users", "email", "TEXT UNIQUE NOT NULL DEFAULT ''");

            // Create Expenses table
            s.execute("""
                CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    amount REAL,
                    category TEXT,
                    description TEXT,
                    date TEXT,
                    user_id INTEGER,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);

            // Create Income table
            s.execute("""
                CREATE TABLE IF NOT EXISTS income (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    amount REAL,
                    category TEXT,
                    description TEXT,
                    date TEXT,
                    user_id INTEGER,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);

            System.out.println("Database sync successful.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ensureColumnExists(Connection conn, String table, String column, String definition) {
        try (Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery("PRAGMA table_info(" + table + ")");
            boolean exists = false;
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                s.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
                System.out.println("Migrated: Added " + column + " to " + table);
            }
        } catch (Exception e) {
            // Column might already exist
        }
    }
}