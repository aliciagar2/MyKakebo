package com.aliciagar2.mykakebo.service;

import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SummaryServiceTest {

    @Test
    void spendByCategorySumsExpensesByCategory() {
        List<Expense> expenses = List.of(
                new Expense(null, null, null, KakeboCategory.SURVIVAL, new BigDecimal("10.25"), null, null),
                new Expense(null, null, null, KakeboCategory.SURVIVAL, new BigDecimal("5.25"), null, null),
                new Expense(null, null, null, KakeboCategory.CULTURE, new BigDecimal("3.00"), null, null)
        );

        Map<KakeboCategory, BigDecimal> result = new SummaryService().spendByCategory(expenses);

        assertEquals(Map.of(
                KakeboCategory.SURVIVAL, new BigDecimal("15.50"),
                KakeboCategory.CULTURE, new BigDecimal("3.00")
        ), result);
    }

        @Test
        void spendByCategorySumsRealisticSpanishMonthlyExpenses() {
        User user = new User(1L, "maria.garcia@example.com", "password-hash", "salt");
        YearMonth month = YearMonth.of(2026, 8);
        List<Expense> expenses = List.of(
            new Expense(1L, user, month, KakeboCategory.SURVIVAL, new BigDecimal("750.00"),
                LocalDate.of(2026, 8, 1), "Alquiler"),
            new Expense(2L, user, month, KakeboCategory.SURVIVAL, new BigDecimal("280.00"),
                LocalDate.of(2026, 8, 3), "Compra semanal"),
            new Expense(3L, user, month, KakeboCategory.SURVIVAL, new BigDecimal("110.00"),
                LocalDate.of(2026, 8, 5), "Luz, agua e internet"),
            new Expense(4L, user, month, KakeboCategory.SURVIVAL, new BigDecimal("55.00"),
                LocalDate.of(2026, 8, 8), "Abono transporte"),
            new Expense(5L, user, month, KakeboCategory.OPTIONAL, new BigDecimal("90.00"),
                LocalDate.of(2026, 8, 15), "Cena con amigos"),
            new Expense(6L, user, month, KakeboCategory.CULTURE, new BigDecimal("35.00"),
                LocalDate.of(2026, 8, 20), "Libro"));

        Map<KakeboCategory, BigDecimal> result = new SummaryService().spendByCategory(expenses);

        assertEquals(Map.of(
            KakeboCategory.SURVIVAL, new BigDecimal("1195.00"),
            KakeboCategory.OPTIONAL, new BigDecimal("90.00"),
            KakeboCategory.CULTURE, new BigDecimal("35.00")
        ), result);
        }

        @Test
        void spendByCategoryReturnsEmptyMapForNoExpenses() {
        assertEquals(Map.of(), new SummaryService().spendByCategory(List.of()));
        }
}