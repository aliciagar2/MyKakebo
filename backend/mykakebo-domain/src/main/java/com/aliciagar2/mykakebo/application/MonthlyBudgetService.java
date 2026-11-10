package com.aliciagar2.mykakebo.application;

import com.aliciagar2.mykakebo.application.port.MonthlyBudgetRepositoryPort;
import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetAlreadyExistsException;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.YearMonthInvalidException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.YearMonth;

public class MonthlyBudgetService {

    private final MonthlyBudgetRepositoryPort monthlyBudgetRepositoryPort;

    public MonthlyBudgetService(MonthlyBudgetRepositoryPort monthlyBudgetRepositoryPort) {
        this.monthlyBudgetRepositoryPort = monthlyBudgetRepositoryPort;
    }

    public MonthlyBudget get(int year, int month) {
        YearMonth yearMonth = parseYearMonth(year, month);
        return monthlyBudgetRepositoryPort.findByYearMonth(yearMonth)
                .orElseThrow(() -> new MonthlyBudgetNotFoundException("No budget set for " + yearMonth));
    }

    public MonthlyBudget create(int year, int month, BigDecimal income, BigDecimal fixedExpenses,
                                BigDecimal savingsGoal) {
        YearMonth yearMonth = parseYearMonth(year, month);

        if (monthlyBudgetRepositoryPort.findByYearMonth(yearMonth).isPresent()) {
            throw new MonthlyBudgetAlreadyExistsException("Budget already set for " + yearMonth);
        }

        // A concurrent request can still create the budget after the check above: save() then
        // throws MonthlyBudgetAlreadyExistsException itself, so both paths end in the same error.
        return monthlyBudgetRepositoryPort.save(
                new MonthlyBudget(null, null, yearMonth, income, fixedExpenses, savingsGoal));
    }

    private static YearMonth parseYearMonth(int year, int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException e) {
            throw new YearMonthInvalidException("Invalid year/month: " + year + "/" + month, e);
        }
    }
}
