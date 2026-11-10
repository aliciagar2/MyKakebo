package com.aliciagar2.mykakebo.application;

import com.aliciagar2.mykakebo.adapter.persistence.memory.InMemoryMonthlyBudgetRepositoryAdapter;
import com.aliciagar2.mykakebo.application.port.MonthlyBudgetRepositoryPort;
import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetAlreadyExistsException;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.YearMonthInvalidException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MonthlyBudgetServiceTest {

    @Test
    void createAbortsWhenConcurrentRequestWinsTheRace() {
        MonthlyBudgetService service = new MonthlyBudgetService(new RaceConditionPort());

        assertThatThrownBy(() -> create(service, 2026, 7))
                .isInstanceOf(MonthlyBudgetAlreadyExistsException.class);
    }

    @Test
    void createPersistsBudgetAndAssignsId() {
        MonthlyBudgetService service = newService();

        MonthlyBudget created = create(service, 2026, 3);

        assertThat(created.id()).isNotNull();
        assertThat(created.monthYear()).isEqualTo(YearMonth.of(2026, 3));
        assertThat(created.availableToSpend()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void createRejectsBudgetThatAlreadyExistsForMonth() {
        MonthlyBudgetService service = newService();
        create(service, 2026, 1);

        assertThatThrownBy(() -> create(service, 2026, 1))
                .isInstanceOf(MonthlyBudgetAlreadyExistsException.class);
    }

    @Test
    void createRejectsInvalidMonth() {
        MonthlyBudgetService service = newService();

        assertThatThrownBy(() -> create(service, 2026, 0))
                .isInstanceOf(YearMonthInvalidException.class);
    }

    @Test
    void getRejectsInvalidMonth() {
        MonthlyBudgetService service = newService();

        assertThatThrownBy(() -> service.get(2026, 13))
                .isInstanceOf(YearMonthInvalidException.class);
    }

    @Test
    void getRejectsMonthWithNoBudget() {
        MonthlyBudgetService service = newService();

        assertThatThrownBy(() -> service.get(2026, 6))
                .isInstanceOf(MonthlyBudgetNotFoundException.class);
    }

    @Test
    void getReturnsBudgetForMonth() {
        MonthlyBudgetService service = newService();
        MonthlyBudget created = create(service, 2026, 1);

        assertThat(service.get(2026, 1)).isEqualTo(created);
    }

    private static MonthlyBudget create(MonthlyBudgetService service, int year, int month) {
        return service.create(year, month, new BigDecimal("2000.00"), new BigDecimal("800.00"),
                new BigDecimal("200.00"));
    }

    private static MonthlyBudgetService newService() {
        return new MonthlyBudgetService(new InMemoryMonthlyBudgetRepositoryAdapter());
    }

    // Reports no budget on lookup, then inserts one before the caller's save reaches the adapter,
    // which reproduces the concurrent-insert race in a single thread.
    private static class RaceConditionPort implements MonthlyBudgetRepositoryPort {

        private final InMemoryMonthlyBudgetRepositoryAdapter delegate = new InMemoryMonthlyBudgetRepositoryAdapter();

        @Override
        public Optional<MonthlyBudget> findByYearMonth(YearMonth yearMonth) {
            Optional<MonthlyBudget> found = delegate.findByYearMonth(yearMonth);
            delegate.save(new MonthlyBudget(null, null, yearMonth, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO));
            return found;
        }

        @Override
        public MonthlyBudget save(MonthlyBudget budget) {
            return delegate.save(budget);
        }
    }
}
