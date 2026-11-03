package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.ExpenseEntity;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseConcurrentlyModifiedException;
import com.aliciagar2.mykakebo.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaExpenseRepositoryAdapterTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseEntityMapper mapper;

    @InjectMocks
    private JpaExpenseRepositoryAdapter adapter;

    @Test
    void deleteByIdDelegatesToRepository() {
        adapter.deleteById(5L);

        verify(expenseRepository).deleteById(5L);
    }

    @Test
    void existsByIdDelegatesToRepository() {
        when(expenseRepository.existsById(5L)).thenReturn(true);

        assertThat(adapter.existsById(5L)).isTrue();
    }

    @Test
    void findByDateRangeAndCategoryMapsEntitiesToDomain() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        ExpenseEntity entity = new ExpenseEntity(
                1L, KakeboCategory.OPTIONAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        Expense expense = new Expense(1L, null, YearMonth.of(2024, 1), KakeboCategory.OPTIONAL,
                new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        when(expenseRepository.findByExpenseDateBetweenAndCategory(start, end, KakeboCategory.OPTIONAL))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expense);

        List<Expense> result = adapter.findByDateRangeAndCategory(start, end, KakeboCategory.OPTIONAL);

        assertThat(result).containsExactly(expense);
    }

    @Test
    void findByDateRangeMapsEntitiesToDomain() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        ExpenseEntity entity = new ExpenseEntity(
                1L, KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        Expense expense = new Expense(1L, null, YearMonth.of(2024, 1), KakeboCategory.SURVIVAL,
                new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        when(expenseRepository.findByExpenseDateBetween(start, end)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expense);

        List<Expense> result = adapter.findByDateRange(start, end);

        assertThat(result).containsExactly(expense);
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThat(adapter.findById(1L)).isEmpty();
    }

    @Test
    void findByIdReturnsMappedExpenseWhenFound() {
        ExpenseEntity entity = new ExpenseEntity(
                1L, KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        Expense expense = new Expense(1L, null, YearMonth.of(2024, 1), KakeboCategory.SURVIVAL,
                new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expense);

        assertThat(adapter.findById(1L)).contains(expense);
    }

    @Test
    void saveCreatesNewEntityWhenIdIsNull() {
        Expense newExpense = new Expense(null, null, YearMonth.of(2024, 1), KakeboCategory.SURVIVAL,
                new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        ExpenseEntity newEntity = new ExpenseEntity(
                null, KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        ExpenseEntity savedEntity = new ExpenseEntity(
                1L, KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        Expense savedExpense = new Expense(1L, null, YearMonth.of(2024, 1), KakeboCategory.SURVIVAL,
                new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        when(mapper.toNewEntity(newExpense)).thenReturn(newEntity);
        when(expenseRepository.save(newEntity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedExpense);

        Expense result = adapter.save(newExpense);

        assertThat(result).isEqualTo(savedExpense);
    }

    @Test
    void saveRejectsWhenOptimisticLockFails() {
        Expense expense = new Expense(1L, null, YearMonth.of(2024, 1), KakeboCategory.SURVIVAL,
                new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        ExpenseEntity entity = new ExpenseEntity(
                1L, KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(expenseRepository.save(entity)).thenThrow(new ObjectOptimisticLockingFailureException("", null));

        assertThatThrownBy(() -> adapter.save(expense)).isInstanceOf(ExpenseConcurrentlyModifiedException.class);
    }

    @Test
    void saveRejectsWhenRowNotFound() {
        Expense expense = new Expense(1L, null, YearMonth.of(2024, 1), KakeboCategory.SURVIVAL,
                new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.save(expense)).isInstanceOf(ExpenseConcurrentlyModifiedException.class);
    }

    @Test
    void saveUpdatesExistingEntityWhenIdIsPresent() {
        Expense expense = new Expense(1L, null, YearMonth.of(2024, 1), KakeboCategory.OPTIONAL,
                new BigDecimal("20.00"), LocalDate.of(2024, 1, 20), "updated");
        ExpenseEntity entity = new ExpenseEntity(
                1L, KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "original");
        Expense savedExpense = new Expense(1L, null, YearMonth.of(2024, 1), KakeboCategory.OPTIONAL,
                new BigDecimal("20.00"), LocalDate.of(2024, 1, 20), "updated");
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(expenseRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(savedExpense);

        Expense result = adapter.save(expense);

        verify(mapper).applyTo(expense, entity);
        assertThat(result).isEqualTo(savedExpense);
    }
}
