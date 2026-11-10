package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.MonthlyBudgetEntity;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonthlyBudgetEntityMapperTest {

    private final MonthlyBudgetEntityMapper mapper = new MonthlyBudgetEntityMapper();

    @Test
    void toDomainLeavesUserNull() {
        MonthlyBudgetEntity entity = new MonthlyBudgetEntity(1L, YearMonth.of(2024, 1),
                new BigDecimal("3000.00"), new BigDecimal("1500.00"), new BigDecimal("500.00"));

        MonthlyBudget budget = mapper.toDomain(entity);

        assertThat(budget.user()).isNull();
    }

    @Test
    void toDomainMapsFields() {
        MonthlyBudgetEntity entity = new MonthlyBudgetEntity(1L, YearMonth.of(2024, 1),
                new BigDecimal("3000.00"), new BigDecimal("1500.00"), new BigDecimal("500.00"));

        MonthlyBudget budget = mapper.toDomain(entity);

        assertThat(budget.id()).isEqualTo(1L);
        assertThat(budget.monthYear()).isEqualTo(YearMonth.of(2024, 1));
        assertThat(budget.income()).isEqualTo(new BigDecimal("3000.00"));
        assertThat(budget.fixedExpenses()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(budget.savingsGoal()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    void toNewEntityLeavesIdNullAndCopiesFields() {
        MonthlyBudget budget = new MonthlyBudget(99L, null, YearMonth.of(2024, 2),
                new BigDecimal("3200.00"), new BigDecimal("1600.00"), new BigDecimal("600.00"));

        MonthlyBudgetEntity entity = mapper.toNewEntity(budget);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getYearMonth()).isEqualTo(YearMonth.of(2024, 2));
        assertThat(entity.getIncome()).isEqualTo(new BigDecimal("3200.00"));
        assertThat(entity.getFixedExpenses()).isEqualTo(new BigDecimal("1600.00"));
        assertThat(entity.getSavingsGoal()).isEqualTo(new BigDecimal("600.00"));
    }
}
