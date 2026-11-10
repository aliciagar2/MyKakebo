package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.application.port.MonthlyBudgetRepositoryPort;
import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetAlreadyExistsException;
import com.aliciagar2.mykakebo.repository.MonthlyBudgetRepository;
import java.time.YearMonth;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class JpaMonthlyBudgetRepositoryAdapter implements MonthlyBudgetRepositoryPort {

    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final MonthlyBudgetEntityMapper mapper;

    public JpaMonthlyBudgetRepositoryAdapter(MonthlyBudgetRepository monthlyBudgetRepository,
                                             MonthlyBudgetEntityMapper mapper) {
        this.monthlyBudgetRepository = monthlyBudgetRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<MonthlyBudget> findByYearMonth(YearMonth yearMonth) {
        return monthlyBudgetRepository.findByYearMonth(yearMonth).map(mapper::toDomain);
    }

    @Override
    public MonthlyBudget save(MonthlyBudget budget) {
        try {
            return mapper.toDomain(monthlyBudgetRepository.save(mapper.toNewEntity(budget)));
        } catch (DataIntegrityViolationException e) {
            // The unique constraint on year_month is the real guard against a concurrent insert.
            throw new MonthlyBudgetAlreadyExistsException("Budget already set for " + budget.monthYear(), e);
        }
    }
}
