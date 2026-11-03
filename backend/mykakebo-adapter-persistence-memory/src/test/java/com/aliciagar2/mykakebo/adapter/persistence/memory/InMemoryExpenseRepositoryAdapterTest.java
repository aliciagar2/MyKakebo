package com.aliciagar2.mykakebo.adapter.persistence.memory;

import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseConcurrentlyModifiedException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryExpenseRepositoryAdapterTest {

    private final InMemoryExpenseRepositoryAdapter adapter = new InMemoryExpenseRepositoryAdapter();

    @Test
    void deleteByIdRemovesExpense() {
        Expense saved = adapter.save(expense(null, LocalDate.of(2024, 1, 10), KakeboCategory.SURVIVAL));

        adapter.deleteById(saved.id());

        assertThat(adapter.existsById(saved.id())).isFalse();
    }

    @Test
    void existsByIdReturnsFalseWhenAbsent() {
        assertThat(adapter.existsById(123L)).isFalse();
    }

    @Test
    void existsByIdReturnsTrueWhenPresent() {
        Expense saved = adapter.save(expense(null, LocalDate.of(2024, 1, 10), KakeboCategory.SURVIVAL));

        assertThat(adapter.existsById(saved.id())).isTrue();
    }

    @Test
    void findByDateRangeAndCategoryFiltersByCategoryAndRange() {
        adapter.save(expense(null, LocalDate.of(2024, 1, 10), KakeboCategory.SURVIVAL));
        Expense inRangeOptional = adapter.save(expense(null, LocalDate.of(2024, 1, 15), KakeboCategory.OPTIONAL));
        adapter.save(expense(null, LocalDate.of(2024, 2, 1), KakeboCategory.OPTIONAL));

        var result = adapter.findByDateRangeAndCategory(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), KakeboCategory.OPTIONAL);

        assertThat(result).containsExactly(inRangeOptional);
    }

    @Test
    void findByDateRangeIncludesBoundaryDatesAndExcludesOutOfRangeDates() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        Expense onStart = adapter.save(expense(null, start, KakeboCategory.SURVIVAL));
        Expense onEnd = adapter.save(expense(null, end, KakeboCategory.SURVIVAL));
        adapter.save(expense(null, start.minusDays(1), KakeboCategory.SURVIVAL));
        adapter.save(expense(null, end.plusDays(1), KakeboCategory.SURVIVAL));

        var result = adapter.findByDateRange(start, end);

        assertThat(result).containsExactlyInAnyOrder(onStart, onEnd);
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        assertThat(adapter.findById(123L)).isEmpty();
    }

    @Test
    void findByIdReturnsExpenseWhenPresent() {
        Expense saved = adapter.save(expense(null, LocalDate.of(2024, 1, 10), KakeboCategory.SURVIVAL));

        assertThat(adapter.findById(saved.id())).contains(saved);
    }

    @Test
    void saveAssignsSequentialIdsWhenIdIsNull() {
        Expense first = adapter.save(expense(null, LocalDate.of(2024, 1, 10), KakeboCategory.SURVIVAL));
        Expense second = adapter.save(expense(null, LocalDate.of(2024, 1, 11), KakeboCategory.SURVIVAL));

        assertThat(first.id()).isEqualTo(1L);
        assertThat(second.id()).isEqualTo(2L);
    }

    @Test
    void saveOverwritesExistingExpenseWhenIdIsPresent() {
        Expense saved = adapter.save(expense(null, LocalDate.of(2024, 1, 10), KakeboCategory.SURVIVAL));
        Expense updated = expense(saved.id(), LocalDate.of(2024, 1, 20), KakeboCategory.OPTIONAL);

        Expense result = adapter.save(updated);

        assertThat(result).isEqualTo(updated);
        assertThat(adapter.findById(saved.id())).contains(updated);
    }

    @Test
    void saveThrowsConcurrentlyModifiedExceptionWhenIdNotFound() {
        Expense unknown = expense(123L, LocalDate.of(2024, 1, 10), KakeboCategory.SURVIVAL);

        assertThatThrownBy(() -> adapter.save(unknown)).isInstanceOf(ExpenseConcurrentlyModifiedException.class);
    }

    private static Expense expense(Long id, LocalDate date, KakeboCategory category) {
        return new Expense(id, null, YearMonth.from(date), category, new BigDecimal("10.00"), date, "note");
    }
}
