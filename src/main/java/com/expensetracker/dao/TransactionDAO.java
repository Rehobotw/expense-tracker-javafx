package com.expensetracker.dao;

import com.expensetracker.model.Transaction;
import java.sql.*;
import java.util.*;

public class TransactionDAO {

    public List<Transaction> getAllTransactions(int userId) {
        String sql = """
            SELECT id, date, category, description, amount, 'Expense' as type 
            FROM expenses WHERE user_id = ?
            UNION ALL
            SELECT id, date, category, description, amount, 'Income' as type 
            FROM income WHERE user_id = ?
            ORDER BY date DESC
        """;

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getString("category"),
                        rs.getString("description"),
                        Math.abs(rs.getDouble("amount")), // Always positive for display
                        rs.getString("type")
                ));
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch transactions", ex);
        }

        return transactions;
    }

    public boolean deleteTransaction(int transactionId, String type, int userId) {
        String tableName = type.equalsIgnoreCase("Income") ? "income" : "expenses";
        String sql = "DELETE FROM " + tableName + " WHERE id = ? AND user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transactionId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            throw new RuntimeException("Failed to delete transaction", ex);
        }
    }
}