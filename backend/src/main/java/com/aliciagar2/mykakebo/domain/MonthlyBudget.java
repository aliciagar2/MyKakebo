package com.aliciagar2.mykakebo.domain;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyBudget(
        Long id,
        User user,
        YearMonth monthYear,
        BigDecimal income,
        BigDecimal fixedExpenses,
        BigDecimal savingsGoal
) {
    public BigDecimal availableToSpend() {
        return income.subtract(fixedExpenses).subtract(savingsGoal);
    }
}