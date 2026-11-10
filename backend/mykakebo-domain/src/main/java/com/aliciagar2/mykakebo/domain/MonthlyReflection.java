package com.aliciagar2.mykakebo.domain;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyReflection(
        Long id,
        User user,
        YearMonth monthYear,
        BigDecimal moneyHad,
        BigDecimal moneySaved,
        BigDecimal moneySpent,
        String improvementNote
) {
}