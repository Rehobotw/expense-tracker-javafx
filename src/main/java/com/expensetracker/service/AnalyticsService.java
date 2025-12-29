package com.expensetracker.service;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.dao.IncomeDAO;

import java.util.Map;

public class AnalyticsService {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final IncomeDAO incomeDAO = new IncomeDAO();

    public double getTotalIncome(int userId) {
        return incomeDAO.getTotalIncome(userId);
    }

    public double getTotalExpenses(int userId) {
        return expenseDAO.getTotalExpenses(userId);
    }

    public double getBalance(int userId) {
        return getTotalIncome(userId) - getTotalExpenses(userId);
    }

    public Map<String, Double> getExpensesByCategory(int userId) {
        return expenseDAO.getExpensesByCategory(userId);
    }

    public Map<String, Double> getMonthlyExpenses(int userId) {
        return expenseDAO.getMonthlyExpenses(userId);
    }
}
