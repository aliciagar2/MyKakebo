package com.aliciagar2.mykakebo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MonthlyBudgetRequest(
        @NotNull @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal income,
        @NotNull @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal fixedExpenses,
        @NotNull @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal savingsGoal
) {}