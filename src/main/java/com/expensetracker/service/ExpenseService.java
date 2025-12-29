package com.expensetracker.service;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.dao.IncomeDAO;
import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.model.Transaction;
import com.expensetracker.util.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExpenseService {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final IncomeDAO incomeDAO = new IncomeDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    private int getValidatedUserId() {
        Integer userId = SessionManager.getUserId();
        if (userId == null || userId <= 0)
            throw new IllegalStateException("User is not logged in or session expired");
        return userId;
    }

    /* ============================
       TRANSACTION HISTORY METHODS
       ============================ */

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = transactionDAO.getAllTransactions(getValidatedUserId());
        return list != null ? list : Collections.emptyList();
    }

    public boolean deleteTransaction(int transactionId, String type) {
        return transactionDAO.deleteTransaction(transactionId, type, getValidatedUserId());
    }

    /* ============================
       ORIGINAL EXPENSE METHODS (unchanged except for small updates)
       ============================ */

    public void addExpense(Expense expense) {
        validate(expense);
        expenseDAO.addExpense(expense, getValidatedUserId());
    }

    public List<Expense> getAllExpenses() {
        List<Expense> list = expenseDAO.getAll(getValidatedUserId());
        return list != null ? list : Collections.emptyList();
    }

    public double getTotalExpenses() {
        return expenseDAO.getTotalExpenses(getValidatedUserId());
    }

    public List<Expense> getRecentExpenses(int limit) {
        List<Expense> list = expenseDAO.getRecentExpenses(getValidatedUserId(), limit);
        return list != null ? list : Collections.emptyList();
    }

    public Map<String, Double> getExpensesByCategory() {
        Map<String, Double> map = expenseDAO.getExpensesByCategory(getValidatedUserId());
        return map != null ? map : Collections.emptyMap();
    }

    public Map<String, Double> getMonthlyExpenses() {
        Map<String, Double> map = expenseDAO.getMonthlyExpenses(getValidatedUserId());
        return map != null ? map : Collections.emptyMap();
    }

    public boolean updateExpense(Expense expense) {
        validate(expense);
        return expenseDAO.updateExpense(expense, getValidatedUserId());
    }

    public boolean deleteExpense(int expenseId) {
        return expenseDAO.deleteExpense(expenseId, getValidatedUserId());
    }

    private void validate(Expense expense) {
        if (expense == null)
            throw new IllegalArgumentException("Expense cannot be null");

        if (expense.getAmount() <= 0)
            throw new IllegalArgumentException("Expense amount must be positive");

        if (expense.getCategory() == null || expense.getCategory().isBlank())
            throw new IllegalArgumentException("Category is required");
        else
            expense.setCategory(expense.getCategory().trim());

        if (expense.getDate() == null || expense.getDate().isBlank())
            throw new IllegalArgumentException("Date is required");

        try {
            LocalDate.parse(expense.getDate());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }
    }
}