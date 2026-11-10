package com.aliciagar2.mykakebo.domain;

import java.math.BigDecimal;

public record BudgetAllocation(
    BigDecimal survivalPercent,
    BigDecimal optionalPercent,
    BigDecimal culturePercent,
    BigDecimal extraPercent) {
    
    public BudgetAllocation {
        BigDecimal total = survivalPercent.add(optionalPercent)
            .add(culturePercent).add(extraPercent);
        if (total.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalArgumentException(
                "Percentages must sum to 1.0, got: " + total);
        }
    }
    
    public static BudgetAllocation defaultAllocation() {
        return new BudgetAllocation(
            new BigDecimal("0.50"),  // Survival
            new BigDecimal("0.25"),  // Optional
            new BigDecimal("0.15"),  // Culture
            new BigDecimal("0.10")   // Extra
        );
    }
}
