package com.aliciagar2.mykakebo.repository;

import com.aliciagar2.mykakebo.domain.MonthlyBudgetEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MonthlyBudgetRepositoryTest {

    @Autowired
    private MonthlyBudgetRepository monthlyBudgetRepository;

    @Test
    void findByYearMonth_returnsCorrectBudget() {
        YearMonth yearMonth = YearMonth.of(2024, 1);
        MonthlyBudgetEntity budget = new MonthlyBudgetEntity(
                null, yearMonth,
                new BigDecimal("3000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("500.00")
        );
        monthlyBudgetRepository.save(budget);

        Optional<MonthlyBudgetEntity> result = monthlyBudgetRepository.findByYearMonth(yearMonth);

        assertThat(result).isPresent()
                .get()
                .extracting(MonthlyBudgetEntity::getYearMonth)
                .isEqualTo(yearMonth);
    }

    @Test
    void findByYearMonth_returnsEmptyWhenBudgetNotFound() {
        YearMonth searchYearMonth = YearMonth.of(2024, 3);

        Optional<MonthlyBudgetEntity> result = monthlyBudgetRepository.findByYearMonth(searchYearMonth);

        assertThat(result).isEmpty();
    }

    @Test
    void findByYearMonth_findsCorrectBudgetAmongMultiple() {
        YearMonth january = YearMonth.of(2024, 1);
        YearMonth february = YearMonth.of(2024, 2);
        MonthlyBudgetEntity budgetJan = new MonthlyBudgetEntity(
                null, january,
                new BigDecimal("3000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("500.00")
        );
        MonthlyBudgetEntity budgetFeb = new MonthlyBudgetEntity(
                null, february,
                new BigDecimal("3200.00"),
                new BigDecimal("1600.00"),
                new BigDecimal("600.00")
        );
        monthlyBudgetRepository.save(budgetJan);
        monthlyBudgetRepository.save(budgetFeb);

        Optional<MonthlyBudgetEntity> result = monthlyBudgetRepository.findByYearMonth(february);

        assertThat(result).isPresent()
                .get()
                .extracting(MonthlyBudgetEntity::getYearMonth)
                .isEqualTo(february);
    }
}
