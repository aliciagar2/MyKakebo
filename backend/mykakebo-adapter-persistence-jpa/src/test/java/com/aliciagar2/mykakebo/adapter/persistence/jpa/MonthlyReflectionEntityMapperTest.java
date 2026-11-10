package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.domain.MonthlyReflection;
import com.aliciagar2.mykakebo.domain.MonthlyReflectionEntity;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonthlyReflectionEntityMapperTest {

    private final MonthlyReflectionEntityMapper mapper = new MonthlyReflectionEntityMapper();

    @Test
    void toDomainLeavesUserNull() {
        MonthlyReflectionEntity entity = new MonthlyReflectionEntity(1L, YearMonth.of(2024, 1),
                new BigDecimal("3000.00"), new BigDecimal("400.00"), new BigDecimal("1100.00"), "note");

        MonthlyReflection reflection = mapper.toDomain(entity);

        assertThat(reflection.user()).isNull();
    }

    @Test
    void toDomainMapsFields() {
        MonthlyReflectionEntity entity = new MonthlyReflectionEntity(1L, YearMonth.of(2024, 1),
                new BigDecimal("3000.00"), new BigDecimal("400.00"), new BigDecimal("1100.00"), "note");

        MonthlyReflection reflection = mapper.toDomain(entity);

        assertThat(reflection.id()).isEqualTo(1L);
        assertThat(reflection.monthYear()).isEqualTo(YearMonth.of(2024, 1));
        assertThat(reflection.moneyHad()).isEqualTo(new BigDecimal("3000.00"));
        assertThat(reflection.moneySaved()).isEqualTo(new BigDecimal("400.00"));
        assertThat(reflection.moneySpent()).isEqualTo(new BigDecimal("1100.00"));
        assertThat(reflection.improvementNote()).isEqualTo("note");
    }

    @Test
    void toEntityKeepsIdAndCopiesFields() {
        MonthlyReflection reflection = new MonthlyReflection(7L, null, YearMonth.of(2024, 2),
                new BigDecimal("3200.00"), new BigDecimal("450.00"), new BigDecimal("1150.00"), "spend less");

        MonthlyReflectionEntity entity = mapper.toEntity(reflection);

        assertThat(entity.getId()).isEqualTo(7L);
        assertThat(entity.getYearMonth()).isEqualTo(YearMonth.of(2024, 2));
        assertThat(entity.getMoneyHad()).isEqualTo(new BigDecimal("3200.00"));
        assertThat(entity.getMoneySaved()).isEqualTo(new BigDecimal("450.00"));
        assertThat(entity.getMoneySpent()).isEqualTo(new BigDecimal("1150.00"));
        assertThat(entity.getImprovementNote()).isEqualTo("spend less");
    }
}
