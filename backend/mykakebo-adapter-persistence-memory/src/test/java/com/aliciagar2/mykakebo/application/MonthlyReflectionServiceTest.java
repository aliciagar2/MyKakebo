package com.aliciagar2.mykakebo.application;

import com.aliciagar2.mykakebo.adapter.persistence.memory.InMemoryMonthlyReflectionRepositoryAdapter;
import com.aliciagar2.mykakebo.domain.MonthlyReflection;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyReflectionNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.YearMonthInvalidException;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MonthlyReflectionServiceTest {

    @Test
    void getRejectsInvalidMonth() {
        MonthlyReflectionService service = newService();

        assertThatThrownBy(() -> service.get(2026, 13))
                .isInstanceOf(YearMonthInvalidException.class);
    }

    @Test
    void getRejectsMonthWithNoReflection() {
        MonthlyReflectionService service = newService();

        assertThatThrownBy(() -> service.get(2026, 6))
                .isInstanceOf(MonthlyReflectionNotFoundException.class);
    }

    @Test
    void getReturnsReflectionForMonth() {
        MonthlyReflectionService service = newService();
        MonthlyReflection saved = service.save(reflection(2026, 2));

        assertThat(service.get(2026, 2)).isEqualTo(saved);
    }

    @Test
    void listReturnsReflectionsOrderedByMonth() {
        MonthlyReflectionService service = newService();
        service.save(reflection(2026, 3));
        service.save(reflection(2026, 1));
        service.save(reflection(2026, 2));

        assertThat(service.list()).extracting(MonthlyReflection::monthYear)
                .containsExactly(YearMonth.of(2026, 1), YearMonth.of(2026, 2), YearMonth.of(2026, 3));
    }

    private static MonthlyReflectionService newService() {
        return new MonthlyReflectionService(new InMemoryMonthlyReflectionRepositoryAdapter());
    }

    private static MonthlyReflection reflection(int year, int month) {
        return new MonthlyReflection(null, null, YearMonth.of(year, month), new BigDecimal("2000.00"),
                new BigDecimal("500.00"), new BigDecimal("700.00"), null);
    }

    @Test
    void savePersistsReflectionAndAssignsId() {
        MonthlyReflectionService service = newService();

        MonthlyReflection saved = service.save(reflection(2026, 4));

        assertThat(saved.id()).isNotNull();
        assertThat(saved.moneySpent()).isEqualTo(new BigDecimal("700.00"));
    }
}
