package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.ExpenseEntity;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseEntityMapperTest {

    private final ExpenseEntityMapper mapper = new ExpenseEntityMapper();

    @Test
    void applyToUpdatesEntityFieldsFromExpense() {
        ExpenseEntity entity = new ExpenseEntity(
                7L, KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 1), "original");
        Expense expense = new Expense(7L, null, YearMonth.of(2024, 2), KakeboCategory.OPTIONAL,
                new BigDecimal("20.00"), LocalDate.of(2024, 2, 10), "updated");

        mapper.applyTo(expense, entity);

        assertThat(entity.getId()).isEqualTo(7L);
        assertThat(entity.getCategory()).isEqualTo(KakeboCategory.OPTIONAL);
        assertThat(entity.getAmount()).isEqualTo(new BigDecimal("20.00"));
        assertThat(entity.getExpenseDate()).isEqualTo(LocalDate.of(2024, 2, 10));
        assertThat(entity.getNote()).isEqualTo("updated");
    }

    @Test
    void toDomainLeavesUserNull() {
        ExpenseEntity entity = new ExpenseEntity(
                1L, KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 15), "note");

        Expense expense = mapper.toDomain(entity);

        assertThat(expense.user()).isNull();
    }

    @Test
    void toDomainMapsFieldsAndDerivesMonthYearFromDate() {
        ExpenseEntity entity = new ExpenseEntity(
                1L, KakeboCategory.CULTURE, new BigDecimal("15.50"), LocalDate.of(2024, 3, 20), "books");

        Expense expense = mapper.toDomain(entity);

        assertThat(expense.id()).isEqualTo(1L);
        assertThat(expense.category()).isEqualTo(KakeboCategory.CULTURE);
        assertThat(expense.amount()).isEqualTo(new BigDecimal("15.50"));
        assertThat(expense.date()).isEqualTo(LocalDate.of(2024, 3, 20));
        assertThat(expense.note()).isEqualTo("books");
        assertThat(expense.monthYear()).isEqualTo(YearMonth.of(2024, 3));
    }

    @Test
    void toNewEntityLeavesIdNullAndCopiesFields() {
        Expense expense = new Expense(99L, null, YearMonth.of(2024, 4), KakeboCategory.EXTRA,
                new BigDecimal("5.00"), LocalDate.of(2024, 4, 1), "gift");

        ExpenseEntity entity = mapper.toNewEntity(expense);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getCategory()).isEqualTo(KakeboCategory.EXTRA);
        assertThat(entity.getAmount()).isEqualTo(new BigDecimal("5.00"));
        assertThat(entity.getExpenseDate()).isEqualTo(LocalDate.of(2024, 4, 1));
        assertThat(entity.getNote()).isEqualTo("gift");
    }
}
