package com.aliciagar2.mykakebo.application;

import com.aliciagar2.mykakebo.adapter.persistence.memory.InMemoryExpenseRepositoryAdapter;
import com.aliciagar2.mykakebo.application.port.ExpenseRepositoryPort;
import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseDateOutsideMonthException;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.YearMonthInvalidException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpenseServiceTest {

    @Test
    void createPersistsExpenseAndAssignsId() {
        ExpenseService service = newService();

        Expense created = service.create(2026, 1, KakeboCategory.SURVIVAL, new BigDecimal("42.50"),
                LocalDate.of(2026, 1, 10), "Rent");

        assertThat(created.id()).isNotNull();
        assertThat(created.category()).isEqualTo(KakeboCategory.SURVIVAL);
        assertThat(created.amount()).isEqualTo(new BigDecimal("42.50"));
        assertThat(created.date()).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(created.note()).isEqualTo("Rent");
    }

    @Test
    void createRejectsDateOutsideMonth() {
        ExpenseService service = newService();

        assertThatThrownBy(() -> service.create(2026, 1, KakeboCategory.SURVIVAL, new BigDecimal("10.00"),
                LocalDate.of(2026, 6, 15), null))
                .isInstanceOf(ExpenseDateOutsideMonthException.class);
    }

    @Test
    void createRejectsInvalidYearMonth() {
        ExpenseService service = newService();

        assertThatThrownBy(() -> service.create(2026, 13, KakeboCategory.SURVIVAL, new BigDecimal("10.00"),
                LocalDate.of(2026, 1, 1), null))
                .isInstanceOf(YearMonthInvalidException.class);
    }

    @Test
    void deleteRejectsWhenExpenseDoesNotExist() {
        ExpenseService service = newService();

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(ExpenseNotFoundException.class);
    }

    @Test
    void deleteRemovesExpense() {
        InMemoryExpenseRepositoryAdapter adapter = new InMemoryExpenseRepositoryAdapter();
        ExpenseService service = new ExpenseService(adapter);
        Expense created = service.create(2026, 1, KakeboCategory.SURVIVAL, new BigDecimal("10.00"),
                LocalDate.of(2026, 1, 5), null);

        service.delete(created.id());

        assertThat(adapter.existsById(created.id())).isFalse();
    }

    @Test
    void listFiltersByCategoryWhenProvided() {
        ExpenseService service = newService();
        service.create(2026, 1, KakeboCategory.SURVIVAL, new BigDecimal("50.00"), LocalDate.of(2026, 1, 15),
                "Groceries");
        service.create(2026, 1, KakeboCategory.OPTIONAL, new BigDecimal("30.00"), LocalDate.of(2026, 1, 20),
                "Dining");

        List<Expense> result = service.list(2026, 1, KakeboCategory.OPTIONAL);

        assertThat(result).hasSize(1)
                .extracting(Expense::category)
                .containsOnly(KakeboCategory.OPTIONAL);
    }

    @Test
    void listRejectsInvalidYearMonth() {
        ExpenseService service = newService();

        assertThatThrownBy(() -> service.list(2026, 0, null))
                .isInstanceOf(YearMonthInvalidException.class);
    }

    @Test
    void listReturnsExpensesInMonth() {
        ExpenseService service = newService();
        service.create(2026, 1, KakeboCategory.SURVIVAL, new BigDecimal("50.00"), LocalDate.of(2026, 1, 15),
                "Groceries");
        service.create(2026, 2, KakeboCategory.SURVIVAL, new BigDecimal("20.00"), LocalDate.of(2026, 2, 1),
                "Outside January");

        List<Expense> result = service.list(2026, 1, null);

        assertThat(result).hasSize(1)
                .extracting(Expense::note)
                .containsOnly("Groceries");
    }

    private static ExpenseService newService() {
        return new ExpenseService(new InMemoryExpenseRepositoryAdapter());
    }

    // Decorates a real InMemoryExpenseRepositoryAdapter to deterministically reproduce, in a
    // single thread, the race ExpenseService.update() guards against: another request deletes
    // the row between the service's findById() and its save().
    private static final class RaceConditionPort implements ExpenseRepositoryPort {

        private final ExpenseRepositoryPort delegate;

        private RaceConditionPort(ExpenseRepositoryPort delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<Expense> findByDateRange(LocalDate start, LocalDate end) {
            return delegate.findByDateRange(start, end);
        }

        @Override
        public List<Expense> findByDateRangeAndCategory(LocalDate start, LocalDate end, KakeboCategory category) {
            return delegate.findByDateRangeAndCategory(start, end, category);
        }

        @Override
        public Optional<Expense> findById(Long id) {
            Optional<Expense> found = delegate.findById(id);
            delegate.deleteById(id);
            return found;
        }

        @Override
        public Expense save(Expense expense) {
            return delegate.save(expense);
        }

        @Override
        public boolean existsById(Long id) {
            return delegate.existsById(id);
        }

        @Override
        public void deleteById(Long id) {
            delegate.deleteById(id);
        }
    }

    @Test
    void updatePersistsChanges() {
        ExpenseService service = newService();
        Expense created = service.create(2026, 1, KakeboCategory.SURVIVAL, new BigDecimal("10.00"),
                LocalDate.of(2026, 1, 5), "original");

        Expense updated = service.update(2026, 1, created.id(), KakeboCategory.OPTIONAL, new BigDecimal("20.00"),
                LocalDate.of(2026, 1, 10), "updated");

        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.category()).isEqualTo(KakeboCategory.OPTIONAL);
        assertThat(updated.amount()).isEqualTo(new BigDecimal("20.00"));
        assertThat(updated.date()).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(updated.note()).isEqualTo("updated");
    }

    @Test
    void updateRejectsDateOutsideMonth() {
        ExpenseService service = newService();
        Expense created = service.create(2026, 1, KakeboCategory.SURVIVAL, new BigDecimal("10.00"),
                LocalDate.of(2026, 1, 5), null);

        assertThatThrownBy(() -> service.update(2026, 1, created.id(), KakeboCategory.SURVIVAL,
                new BigDecimal("10.00"), LocalDate.of(2026, 6, 15), null))
                .isInstanceOf(ExpenseDateOutsideMonthException.class);
    }

    @Test
    void updateRejectsWhenConcurrentDeleteWinsTheRace() {
        InMemoryExpenseRepositoryAdapter adapter = new InMemoryExpenseRepositoryAdapter();
        Expense created = adapter.save(new Expense(null, null, YearMonth.of(2026, 1),
                KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2026, 1, 5), "original"));
        ExpenseService service = new ExpenseService(new RaceConditionPort(adapter));

        assertThatThrownBy(() -> service.update(2026, 1, created.id(), KakeboCategory.OPTIONAL,
                new BigDecimal("20.00"), LocalDate.of(2026, 1, 10), "updated"))
                .isInstanceOf(ExpenseNotFoundException.class);
    }

    @Test
    void updateRejectsWhenExpenseDoesNotExist() {
        ExpenseService service = newService();

        assertThatThrownBy(() -> service.update(2026, 1, 999L, KakeboCategory.SURVIVAL, new BigDecimal("10.00"),
                LocalDate.of(2026, 1, 1), null))
                .isInstanceOf(ExpenseNotFoundException.class);
    }
}
