package com.aliciagar2.mykakebo.domain;

import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonthlyBudgetTest {

    @Test
    void availableToSpendCanReturnNegativeWhenExpensesExceedIncome() {
        MonthlyBudget budget = budget(new BigDecimal("1000.00"), new BigDecimal("900.00"), new BigDecimal("200.00"));

        assertThat(budget.availableToSpend()).isEqualTo(new BigDecimal("-100.00"));
    }

    @Test
    void availableToSpendSubtractsFixedExpensesAndSavingsGoalFromIncome() {
        MonthlyBudget budget = budget(new BigDecimal("2000.00"), new BigDecimal("800.00"), new BigDecimal("300.00"));

        assertThat(budget.availableToSpend()).isEqualTo(new BigDecimal("900.00"));
    }

    private static MonthlyBudget budget(BigDecimal income, BigDecimal fixedExpenses, BigDecimal savingsGoal) {
        return new MonthlyBudget(1L, null, YearMonth.of(2024, 1), income, fixedExpenses, savingsGoal);
    }
}
