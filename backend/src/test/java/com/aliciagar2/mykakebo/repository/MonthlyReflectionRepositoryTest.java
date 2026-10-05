package com.aliciagar2.mykakebo.repository;

import com.aliciagar2.mykakebo.domain.MonthlyReflectionEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MonthlyReflectionRepositoryTest {

    @Autowired
    private MonthlyReflectionRepository monthlyReflectionRepository;

    @Test
    void findByYearMonthReturnsCorrectReflection() {
        YearMonth yearMonth = YearMonth.of(2024, 1);
        MonthlyReflectionEntity reflection = new MonthlyReflectionEntity(
                null, yearMonth,
                new BigDecimal("3000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("2500.00"),
                "Good month"
        );
        monthlyReflectionRepository.save(reflection);

        Optional<MonthlyReflectionEntity> result = monthlyReflectionRepository.findByYearMonth(yearMonth);

        assertThat(result).isPresent()
                .get()
                .extracting(MonthlyReflectionEntity::getYearMonth)
                .isEqualTo(yearMonth);
    }

    @Test
    void findByYearMonthReturnsEmptyWhenReflectionNotFound() {
        YearMonth searchYearMonth = YearMonth.of(2024, 5);

        Optional<MonthlyReflectionEntity> result = monthlyReflectionRepository.findByYearMonth(searchYearMonth);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByOrderByYearMonthAscReturnsReflectionsInAscendingOrder() {
        MonthlyReflectionEntity reflection1 = new MonthlyReflectionEntity(
                null, YearMonth.of(2024, 3),
                new BigDecimal("3000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("2500.00"),
                "March reflection"
        );
        MonthlyReflectionEntity reflection2 = new MonthlyReflectionEntity(
                null, YearMonth.of(2024, 1),
                new BigDecimal("3000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("2500.00"),
                "January reflection"
        );
        MonthlyReflectionEntity reflection3 = new MonthlyReflectionEntity(
                null, YearMonth.of(2024, 2),
                new BigDecimal("3000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("2500.00"),
                "February reflection"
        );
        monthlyReflectionRepository.save(reflection1);
        monthlyReflectionRepository.save(reflection2);
        monthlyReflectionRepository.save(reflection3);

        List<MonthlyReflectionEntity> result = monthlyReflectionRepository.findAllByOrderByYearMonthAsc();

        assertThat(result).hasSize(3)
                .extracting(MonthlyReflectionEntity::getYearMonth)
                .containsExactly(
                        YearMonth.of(2024, 1),
                        YearMonth.of(2024, 2),
                        YearMonth.of(2024, 3)
                );
    }
}
