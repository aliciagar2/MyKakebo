package com.aliciagar2.mykakebo.service;

import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.MonthlyReflection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SummaryService {

        public Map<KakeboCategory, BigDecimal> spendByCategory(List<Expense> expenses) {
                return expenses.stream()
                                .collect(Collectors.groupingBy(
                                                Expense::category,
                                                Collectors.reducing(
                                                                BigDecimal.ZERO,
                                                                Expense::amount,
                                                                BigDecimal::add)));
        }

        public Map<KakeboCategory, BigDecimal> remainingByCategory(
                        Map<KakeboCategory, BigDecimal> spentByCategory,
                        Map<KakeboCategory, BigDecimal> limitByCategory) {

                return limitByCategory.entrySet().stream()
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                entry -> entry.getValue().subtract(
                                                                spentByCategory.getOrDefault(entry.getKey(),
                                                                                BigDecimal.ZERO))));
        }

        public boolean isOverBudget(
                        Map<KakeboCategory, BigDecimal> spentByCategory,
                        Map<KakeboCategory, BigDecimal> limitByCategory) {
                return isOverBudget(remainingByCategory(spentByCategory, limitByCategory));
        }

        public boolean isOverBudget(Map<KakeboCategory, BigDecimal> remainingByCategory) {
                return remainingByCategory.values().stream()
                                .anyMatch(remaining -> remaining.compareTo(BigDecimal.ZERO) < 0);
        }

        public MonthlyReflection buildReflection(MonthlyBudget budget, List<Expense> expenses) {
                BigDecimal moneySpent = expenses.stream()
                                .map(Expense::amount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal moneySaved = budget.income()
                                .subtract(budget.fixedExpenses())
                                .subtract(moneySpent);
                String improvementNote = moneySpent.compareTo(budget.income()) > 0
                                ? "Warning: money spent exceeds income."
                                : null;

                return new MonthlyReflection(
                                null,
                                budget.user(),
                                budget.monthYear(),
                                budget.income(),
                                moneySaved,
                                moneySpent,
                                improvementNote);
        }

}