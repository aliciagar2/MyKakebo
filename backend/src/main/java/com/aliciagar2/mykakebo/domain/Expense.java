package com.aliciagar2.mykakebo.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public record Expense(
        Long id,
        User user,
        YearMonth monthYear,
        KakeboCategory category,
        BigDecimal amount,
        LocalDate date,
        String note
) {
}