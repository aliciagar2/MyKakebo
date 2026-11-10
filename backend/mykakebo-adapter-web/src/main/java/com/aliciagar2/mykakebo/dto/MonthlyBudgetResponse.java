package com.aliciagar2.mykakebo.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyBudgetResponse(
        Long id,
        YearMonth month,
        BigDecimal income,
        BigDecimal fixedExpenses,
        BigDecimal savingsGoal,
        BigDecimal availableToSpend
) {}