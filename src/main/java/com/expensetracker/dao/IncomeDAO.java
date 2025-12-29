package com.expensetracker.dao;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;

import java.sql.*;
import java.util.*;

public class IncomeDAO {

    /* ============================
       CREATE
       ============================ */

    public static void addIncome(Income income, int userId) {
        String sql = """
            INSERT INTO income (amount, category, description, date, user_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, Math.abs(income.getAmount())); // always positive
            ps.setString(2, income.getCategory());
            ps.setString(3, income.getDescription());
            ps.setString(4, income.getDate());
            ps.setInt(5, userId);

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to add income", ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* ============================
       READ
       ============================ */

    /** Income History */
    public List<Income> getAll(int userId) {
        String sql = """
            SELECT * FROM income
            WHERE user_id = ?
            ORDER BY date DESC
        """;

        List<Income> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapIncome(rs));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch income records", ex);
        }
        return list;
    }

    /** Dashboard total */
    public double getTotalIncome(int userId) {
        String sql = "SELECT SUM(amount) FROM income WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.getDouble(1);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to calculate total income", ex);
        }
    }

    /** Dashboard recent list */
    public List<Income> getRecentIncome(int userId, int limit) {
        String sql = """
            SELECT * FROM income
            WHERE user_id = ?
            ORDER BY date DESC
            LIMIT ?
        """;

        List<Income> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapIncome(rs));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch recent income", ex);
        }
        return list;
    }


    /* ============================
       ANALYTICS
       ============================ */

    public Map<String, Double> getIncomeByCategory(int userId) {
        String sql = """
            SELECT category, SUM(amount) AS total
            FROM income
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
                        rs.getDouble("total")
                );
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load income category analytics", ex);
        }
        return result;
    }

    public Map<String, Double> getMonthlyIncome(int userId) {
        String sql = """
            SELECT strftime('%Y-%m', date) AS month, SUM(amount) AS total
            FROM income
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
                        rs.getDouble("total")
                );
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load monthly income analytics", ex);
        }
        return result;
    }
    /* ============================
       UPDATE
       ============================ */
    public boolean updateIncome(Income income, int userId) {

        String sql = """
            UPDATE income
            SET amount = ?, category = ?, description = ?, date = ?
            WHERE id = ? AND user_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, -Math.abs(income.getAmount())); // keep DB negative rule
            ps.setString(2, income.getCategory());
            ps.setString(3, income.getDescription());
            ps.setString(4, income.getDate());
            ps.setInt(5, income.getId());
            ps.setInt(6, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            throw new RuntimeException("Failed to update Income", ex);
        }
    }

    /* ============================
       DELETE
       ============================ */

    public void deleteIncome(int incomeId, int userId) {
        String sql = "DELETE FROM income WHERE id = ? AND user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, incomeId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to delete income", ex);
        }
    }

    /* ============================
       MAPPER
       ============================ */

    private Income mapIncome(ResultSet rs) throws SQLException {
        return new Income(
                rs.getInt("id"),                 // ✅ REQUIRED
                rs.getString("date"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getDouble("amount")
        );
    }
}
