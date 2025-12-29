package com.expensetracker.service;

import com.expensetracker.dao.IncomeDAO;
import com.expensetracker.model.Income;
import com.expensetracker.util.SessionManager;

import java.util.List;

public class IncomeService {

    private final IncomeDAO incomeDAO = new IncomeDAO();

    /**
     * Save new income for logged-in user
     */
    public void addIncome(Income income) {
        validate(income);
        incomeDAO.addIncome(income, SessionManager.getUserId());
    }

    /**
     * Get ALL income records
     * (Income History / Analytics)
     */
    public List<Income> getAllIncome() {
        return incomeDAO.getAll(SessionManager.getUserId());
    }

    /**
     * Get TOTAL income
     * (Dashboard summary)
     */
    public double getTotalIncome() {
        return incomeDAO.getTotalIncome(SessionManager.getUserId());
    }

    /**
     * Get RECENT income records
     * (Dashboard recent transactions)
     */
    public List<Income> getRecentIncome(int limit) {
        return incomeDAO.getRecentIncome(SessionManager.getUserId(), limit);
    }
    /* ============================
      UPDATE
      ============================ */
    public boolean updateIncome(Income income) {
        validate(income);
        return  incomeDAO.updateIncome(income, SessionManager.getUserId());
    }

    /* ============================
       DELETE
       ============================ */
    public void deleteIncome(int incomeId) {
        incomeDAO.deleteIncome(incomeId, SessionManager.getUserId());
    }


    /**
     * Business validation
     */
    private void validate(Income income) {
        if (income == null)
            throw new IllegalArgumentException("Income cannot be null");

        if (income.getAmount() <= 0)
            throw new IllegalArgumentException("Income amount must be positive");

        if (income.getCategory() == null || income.getCategory().isBlank())
            throw new IllegalArgumentException("Category is required");

        if (income.getDate() == null || income.getDate().isBlank())
            throw new IllegalArgumentException("Date is required");
    }
}
