package com.expensetracker.dao;

import com.expensetracker.model.Expense;

import java.sql.*;
import java.util.*;

public class ExpenseDAO {

    /* ============================
       CREATE
       ============================ */
    public static void addExpense(Expense e, int userId) {

        String sql = """
            INSERT INTO expenses (amount, category, description, date, user_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Store as NEGATIVE to represent expense
            ps.setDouble(1, -Math.abs(e.getAmount()));
            ps.setString(2, e.getCategory());
            ps.setString(3, e.getDescription());
            ps.setString(4, e.getDate());
            ps.setInt(5, userId);

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to add expense", ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /* ============================
       READ
       ============================ */

    /** Expense History */
    public List<Expense> getAll(int userId) {

        String sql = """
            SELECT * FROM expenses
            WHERE user_id = ?
            ORDER BY date DESC
        """;

        List<Expense> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapExpense(rs));

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to fetch expenses", ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    /** Dashboard Total */
    public double getTotalExpenses(int userId) {

        String sql = "SELECT SUM(amount) FROM expenses WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            return Math.abs(rs.getDouble(1));

        } catch (Exception ex) {
            throw new RuntimeException("Failed to calculate total expenses", ex);
        }
    }

    /** Dashboard: Recent Transactions */
    public List<Expense> getRecentExpenses(int userId, int limit) {

        String sql = """
            SELECT * FROM expenses
            WHERE user_id = ?
            ORDER BY date DESC
            LIMIT ?
        """;

        List<Expense> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapExpense(rs));

        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch recent expenses", ex);
        }

        return list;
    }

    /* ============================
       ANALYTICS
       ============================ */

    public Map<String, Double> getExpensesByCategory(int userId) {

        String sql = """
            SELECT category, SUM(amount) AS total
            FROM expenses
            WHERE user_id = ?
            GROUP BY category
        """;

        Map<String, Double> result = new HashMap<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.put(
                        rs.getString("category"),
                        Math.abs(rs.getDouble("total"))
                );
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to load category analytics", ex);
        }

        return result;
    }

    public Map<String, Double> getMonthlyExpenses(int userId) {

        String sql = """
            SELECT strftime('%Y-%m', date) AS month, SUM(amount) AS total
            FROM expenses
            WHERE user_id = ?
            GROUP BY month
            ORDER BY month
        """;

        Map<String, Double> result = new LinkedHashMap<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.put(
                        rs.getString("month"),
                        Math.abs(rs.getDouble("total"))
                );
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to load monthly analytics", ex);
        }

        return result;
    }

    /* ============================
       UPDATE
       ============================ */
    public boolean updateExpense(Expense e, int userId) {

        String sql = """
            UPDATE expenses
            SET amount = ?, category = ?, description = ?, date = ?
            WHERE id = ? AND user_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, -Math.abs(e.getAmount())); // keep DB negative rule
            ps.setString(2, e.getCategory());
            ps.setString(3, e.getDescription());
            ps.setString(4, e.getDate());
            ps.setInt(5, e.getId());
            ps.setInt(6, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            throw new RuntimeException("Failed to update expense", ex);
        }
    }

    /* ============================
       DELETE
       ============================ */
    public boolean deleteExpense(int expenseId, int userId) {

        String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, expenseId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            throw new RuntimeException("Failed to delete expense", ex);
        }
    }

    /* ============================
       RESULT MAPPER
       ============================ */
    private Expense mapExpense(ResultSet rs) throws SQLException {

        return new Expense(
                rs.getInt("id"),
                rs.getString("date"),
                rs.getString("category"),
                rs.getString("description"),
                Math.abs(rs.getDouble("amount")) // UI always positive
        );
    }
}
