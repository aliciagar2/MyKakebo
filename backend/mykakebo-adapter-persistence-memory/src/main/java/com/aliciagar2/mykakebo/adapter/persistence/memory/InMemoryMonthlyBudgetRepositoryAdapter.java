package com.aliciagar2.mykakebo.adapter.persistence.memory;

import com.aliciagar2.mykakebo.application.port.MonthlyBudgetRepositoryPort;
import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetAlreadyExistsException;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMonthlyBudgetRepositoryAdapter implements MonthlyBudgetRepositoryPort {

    private final Map<YearMonth, MonthlyBudget> budgets = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    @Override
    public Optional<MonthlyBudget> findByYearMonth(YearMonth yearMonth) {
        return Optional.ofNullable(budgets.get(yearMonth));
    }

    @Override
    public MonthlyBudget save(MonthlyBudget budget) {
        MonthlyBudget saved = new MonthlyBudget(idSequence.incrementAndGet(), budget.user(), budget.monthYear(),
                budget.income(), budget.fixedExpenses(), budget.savingsGoal());
        if (budgets.putIfAbsent(budget.monthYear(), saved) != null) {
            throw new MonthlyBudgetAlreadyExistsException("Budget already set for " + budget.monthYear());
        }
        return saved;
    }
}
