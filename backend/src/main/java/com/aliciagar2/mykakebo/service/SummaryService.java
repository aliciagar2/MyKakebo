package com.aliciagar2.mykakebo.service;

import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;

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
                                BigDecimal::add
                        )
                ));
    }
}