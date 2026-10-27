package com.aliciagar2.mykakebo.adapter.persistence.memory;

import com.aliciagar2.mykakebo.application.port.ExpenseRepositoryPort;
import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseConcurrentlyModifiedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryExpenseRepositoryAdapter implements ExpenseRepositoryPort {

    private final Map<Long, Expense> expensesById = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    @Override
    public List<Expense> findByDateRange(LocalDate start, LocalDate end) {
        return expensesById.values().stream()
                .filter(expense -> isWithinRange(expense, start, end))
                .collect(Collectors.toList());
    }

    @Override
    public List<Expense> findByDateRangeAndCategory(LocalDate start, LocalDate end, KakeboCategory category) {
        return expensesById.values().stream()
                .filter(expense -> isWithinRange(expense, start, end) && expense.category() == category)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Expense> findById(Long id) {
        return Optional.ofNullable(expensesById.get(id));
    }

    @Override
    public Expense save(Expense expense) {
        if (expense.id() == null) {
            Expense withId = new Expense(idSequence.incrementAndGet(), expense.user(), expense.monthYear(),
                    expense.category(), expense.amount(), expense.date(), expense.note());
            expensesById.put(withId.id(), withId);
            return withId;
        }

        if (!expensesById.containsKey(expense.id())) {
            throw new ExpenseConcurrentlyModifiedException("Expense not found: " + expense.id());
        }

        expensesById.put(expense.id(), expense);
        return expense;
    }

    @Override
    public boolean existsById(Long id) {
        return expensesById.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        expensesById.remove(id);
    }

    private static boolean isWithinRange(Expense expense, LocalDate start, LocalDate end) {
        LocalDate date = expense.date();
        return !date.isBefore(start) && !date.isAfter(end);
    }
}
