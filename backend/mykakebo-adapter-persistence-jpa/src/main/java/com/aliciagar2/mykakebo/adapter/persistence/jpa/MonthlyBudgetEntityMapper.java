package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.MonthlyBudgetEntity;
import org.springframework.stereotype.Component;

@Component
public class MonthlyBudgetEntityMapper {

    // MonthlyBudgetEntity has no user column, so user is left null.
    public MonthlyBudget toDomain(MonthlyBudgetEntity entity) {
        return new MonthlyBudget(
                entity.getId(),
                null,
                entity.getYearMonth(),
                entity.getIncome(),
                entity.getFixedExpenses(),
                entity.getSavingsGoal());
    }

    public MonthlyBudgetEntity toNewEntity(MonthlyBudget budget) {
        return new MonthlyBudgetEntity(
                null, budget.monthYear(), budget.income(), budget.fixedExpenses(), budget.savingsGoal());
    }
}
