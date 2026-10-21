package com.aliciagar2.mykakebo.application.port;

import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepositoryPort {
    List<Expense> findByDateRange(LocalDate start, LocalDate end);

    List<Expense> findByDateRangeAndCategory(LocalDate start, LocalDate end, KakeboCategory category);

    Optional<Expense> findById(Long id);

    // Throws ExpenseConcurrentlyModifiedException if a concurrent write lost the optimistic-lock race.
    Expense save(Expense expense);

    boolean existsById(Long id);

    void deleteById(Long id);
}
