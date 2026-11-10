package com.aliciagar2.mykakebo.domain;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BudgetAllocationTest {

    @Test
    void constructorAcceptsPercentagesThatSumToOne() {
        BudgetAllocation allocation = new BudgetAllocation(
                new BigDecimal("0.40"), new BigDecimal("0.30"), new BigDecimal("0.20"), new BigDecimal("0.10"));

        assertThat(allocation.survivalPercent()).isEqualTo(new BigDecimal("0.40"));
    }

    @Test
    void constructorRejectsPercentagesThatDoNotSumToOne() {
        assertThatThrownBy(() -> new BudgetAllocation(
                new BigDecimal("0.40"), new BigDecimal("0.30"), new BigDecimal("0.20"), new BigDecimal("0.05")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void defaultAllocationSumsToOne() {
        BudgetAllocation allocation = BudgetAllocation.defaultAllocation();

        BigDecimal total = allocation.survivalPercent()
                .add(allocation.optionalPercent())
                .add(allocation.culturePercent())
                .add(allocation.extraPercent());
        assertThat(total).isEqualTo(new BigDecimal("1.00"));
    }
}
