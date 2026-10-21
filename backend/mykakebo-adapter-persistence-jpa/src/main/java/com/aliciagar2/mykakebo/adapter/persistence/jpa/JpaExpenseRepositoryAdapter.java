package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.application.port.ExpenseRepositoryPort;
import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.ExpenseEntity;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseConcurrentlyModifiedException;
import com.aliciagar2.mykakebo.repository.ExpenseRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

@Repository
public class JpaExpenseRepositoryAdapter implements ExpenseRepositoryPort {

    private final ExpenseRepository expenseRepository;
    private final ExpenseEntityMapper mapper;

    public JpaExpenseRepositoryAdapter(ExpenseRepository expenseRepository, ExpenseEntityMapper mapper) {
        this.expenseRepository = expenseRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Expense> findByDateRange(LocalDate start, LocalDate end) {
        return expenseRepository.findByExpenseDateBetween(start, end).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Expense> findByDateRangeAndCategory(LocalDate start, LocalDate end, KakeboCategory category) {
        return expenseRepository.findByExpenseDateBetweenAndCategory(start, end, category).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Expense save(Expense expense) {
        if (expense.id() == null) {
            return mapper.toDomain(expenseRepository.save(mapper.toNewEntity(expense)));
        }

        ExpenseEntity entity = expenseRepository.findById(expense.id())
                .orElseThrow(() -> new ExpenseConcurrentlyModifiedException("Expense not found: " + expense.id()));
        mapper.applyTo(expense, entity);

        try {
            return mapper.toDomain(expenseRepository.save(entity));
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ExpenseConcurrentlyModifiedException("Expense concurrently modified: " + expense.id(), e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return expenseRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        expenseRepository.deleteById(id);
    }
}
