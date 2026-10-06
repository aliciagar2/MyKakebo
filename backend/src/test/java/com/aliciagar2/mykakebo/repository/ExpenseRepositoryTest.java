package com.aliciagar2.mykakebo.repository;

import com.aliciagar2.mykakebo.domain.ExpenseEntity;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByExpenseDateBetweenReturnsExpensesInDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        ExpenseEntity expense1 = new ExpenseEntity(null, KakeboCategory.SURVIVAL, new BigDecimal("50.00"), LocalDate.of(2024, 1, 15), "Groceries");
        ExpenseEntity expense2 = new ExpenseEntity(null, KakeboCategory.OPTIONAL, new BigDecimal("30.00"), LocalDate.of(2024, 1, 20), "Dining");
        expenseRepository.save(expense1);
        expenseRepository.save(expense2);

        List<ExpenseEntity> result = expenseRepository.findByExpenseDateBetween(startDate, endDate);

        assertThat(result).hasSize(2)
                .extracting(ExpenseEntity::getExpenseDate)
                .allMatch(date -> !date.isBefore(startDate) && !date.isAfter(endDate));
    }

    @Test
    void findByExpenseDateBetweenReturnsEmptyWhenNoExpensesInRange() {
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 29);
        ExpenseEntity expense = new ExpenseEntity(null, KakeboCategory.SURVIVAL, new BigDecimal("50.00"), LocalDate.of(2024, 1, 15), "Groceries");
        expenseRepository.save(expense);

        List<ExpenseEntity> result = expenseRepository.findByExpenseDateBetween(startDate, endDate);

        assertThat(result).isEmpty();
    }

    @Test
    void findByExpenseDateBetweenAndCategoryFiltersByDateRangeAndCategory() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        ExpenseEntity survival = new ExpenseEntity(null, KakeboCategory.SURVIVAL, new BigDecimal("50.00"), LocalDate.of(2024, 1, 15), "Groceries");
        ExpenseEntity optional = new ExpenseEntity(null, KakeboCategory.OPTIONAL, new BigDecimal("30.00"), LocalDate.of(2024, 1, 20), "Dining");
        expenseRepository.save(survival);
        expenseRepository.save(optional);

        List<ExpenseEntity> result = expenseRepository.findByExpenseDateBetweenAndCategory(startDate, endDate, KakeboCategory.SURVIVAL);

        assertThat(result).hasSize(1)
                .extracting(ExpenseEntity::getCategory)
                .containsOnly(KakeboCategory.SURVIVAL);
    }

    @Test
    void savingADetachedExpenseWhoseRowWasDeletedThrowsOptimisticLockingFailure() {
        ExpenseEntity saved = expenseRepository.saveAndFlush(
                new ExpenseEntity(null, KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 1), "original"));
        entityManager.detach(saved);

        expenseRepository.deleteById(saved.getId());
        entityManager.flush();

        saved.setNote("mutated after concurrent delete");

        assertThatThrownBy(() -> {
            expenseRepository.save(saved);
            expenseRepository.flush();
        }).isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}
