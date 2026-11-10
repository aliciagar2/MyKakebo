package com.aliciagar2.mykakebo.application.port;

import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import java.time.YearMonth;
import java.util.Optional;

public interface MonthlyBudgetRepositoryPort {

    Optional<MonthlyBudget> findByYearMonth(YearMonth yearMonth);

    // Throws MonthlyBudgetAlreadyExistsException when a budget for the same month already exists.
    MonthlyBudget save(MonthlyBudget budget);
}
