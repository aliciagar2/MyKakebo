package com.aliciagar2.mykakebo.service;

import com.aliciagar2.mykakebo.domain.BudgetAllocation;
import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.MonthlyReflection;
import com.aliciagar2.mykakebo.domain.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class SummaryServiceTest {

        @Test
        void spendByCategorySumsExpensesByCategory() {
                List<Expense> expenses = List.of(
                                new Expense(null, null, null, KakeboCategory.SURVIVAL, new BigDecimal("10.25"), null,
                                                null),
                                new Expense(null, null, null, KakeboCategory.SURVIVAL, new BigDecimal("5.25"), null,
                                                null),
                                new Expense(null, null, null, KakeboCategory.CULTURE, new BigDecimal("3.00"), null,
                                                null));

                Map<KakeboCategory, BigDecimal> result = new SummaryService().spendByCategory(expenses);

                assertEquals(Map.of(
                                KakeboCategory.SURVIVAL, new BigDecimal("15.50"),
                                KakeboCategory.CULTURE, new BigDecimal("3.00")), result);
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
                                KakeboCategory.CULTURE, new BigDecimal("35.00")), result);
        }

        @Test
        void spendByCategoryReturnsEmptyMapForNoExpenses() {
                assertEquals(Map.of(), new SummaryService().spendByCategory(List.of()));
        }

        @Test
        void remainingByCategorySubtractsSpentAndDefaultsMissingSpentToZero() {
                SummaryService service = new SummaryService();

                Map<KakeboCategory, BigDecimal> result = service.remainingByCategory(
                                Map.of(KakeboCategory.SURVIVAL, new BigDecimal("25.00")),
                                Map.of(
                                                KakeboCategory.SURVIVAL, new BigDecimal("100.00"),
                                                KakeboCategory.CULTURE, new BigDecimal("50.00")));

                assertEquals(Map.of(
                                KakeboCategory.SURVIVAL, new BigDecimal("75.00"),
                                KakeboCategory.CULTURE, new BigDecimal("50.00")), result);
        }

        @Test
        void isOverBudgetUsesNumericComparison() {
                SummaryService service = new SummaryService();
                Map<KakeboCategory, BigDecimal> limits = Map.of(
                                KakeboCategory.SURVIVAL, new BigDecimal("100.00"),
                                KakeboCategory.CULTURE, new BigDecimal("50.00"));

                assertFalse(service.isOverBudget(
                                Map.of(
                                                KakeboCategory.SURVIVAL, new BigDecimal("100.000"),
                                                KakeboCategory.CULTURE, new BigDecimal("49.99")),
                                limits));
                assertTrue(service.isOverBudget(
                                Map.of(KakeboCategory.SURVIVAL, new BigDecimal("100.01")), limits));
        }

        @Test
        void buildReflectionComputesSpentAndSavedMoney() {
                User user = new User(1L, "maria.garcia@example.com", "password-hash", "salt");
                YearMonth month = YearMonth.of(2026, 8);
                MonthlyBudget budget = new MonthlyBudget(
                                1L,
                                user,
                                month,
                                new BigDecimal("2000.00"),
                                new BigDecimal("800.00"),
                                new BigDecimal("300.00"));
                List<Expense> expenses = List.of(
                                new Expense(null, user, month, KakeboCategory.SURVIVAL,
                                                new BigDecimal("450.00"), null, null),
                                new Expense(null, user, month, KakeboCategory.CULTURE,
                                                new BigDecimal("50.00"), null, null));

                MonthlyReflection result = new SummaryService().buildReflection(budget, expenses);

                assertEquals(new BigDecimal("500.00"), result.moneySpent());
                assertEquals(new BigDecimal("700.00"), result.moneySaved());
                assertEquals(user, result.user());
                assertEquals(month, result.monthYear());
                assertNull(result.improvementNote());
        }

        @Test
        void buildReflectionWarnsWhenSpentMoneyExceedsIncome() {
                MonthlyBudget budget = new MonthlyBudget(
                                1L,
                                null,
                                YearMonth.of(2026, 8),
                                new BigDecimal("2000.00"),
                                BigDecimal.ZERO,
                                BigDecimal.ZERO);
                List<Expense> expenses = List.of(
                                new Expense(null, null, null, KakeboCategory.SURVIVAL,
                                                new BigDecimal("1500.00"), null, null),
                                new Expense(null, null, null, KakeboCategory.OPTIONAL,
                                                new BigDecimal("600.00"), null, null));

                MonthlyReflection result = new SummaryService().buildReflection(budget, expenses);

                assertEquals(new BigDecimal("2100.00"), result.moneySpent());
                assertEquals("Warning: money spent exceeds income.", result.improvementNote());
        }

        @Test
        void budgetAllocationRejectsAllocationThatDoesNotSumToOne() {
                assertThrows(IllegalArgumentException.class, () -> new BudgetAllocation(
                                new BigDecimal("0.50"),
                                new BigDecimal("0.25"),
                                new BigDecimal("0.15"),
                                new BigDecimal("0.05") // suma 0.95, no 1.0
                ));
        }
}