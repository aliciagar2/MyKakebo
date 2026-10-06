package com.aliciagar2.mykakebo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MonthlyBudgetRequest(
        @NotNull @PositiveOrZero BigDecimal income,
        @NotNull @PositiveOrZero BigDecimal fixedExpenses,
        @NotNull @PositiveOrZero BigDecimal savingsGoal
) {}