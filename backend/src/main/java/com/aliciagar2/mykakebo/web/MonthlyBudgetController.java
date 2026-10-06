package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.domain.MonthlyBudgetEntity;
import com.aliciagar2.mykakebo.dto.MonthlyBudgetRequest;
import com.aliciagar2.mykakebo.dto.MonthlyBudgetResponse;
import com.aliciagar2.mykakebo.repository.MonthlyBudgetRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/months/{year}/{month}/budget")
public class MonthlyBudgetController {

    private final MonthlyBudgetRepository budgetRepository;

    public MonthlyBudgetController(MonthlyBudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @GetMapping
    public MonthlyBudgetResponse get(@PathVariable int year, @PathVariable int month) {
        YearMonth yearMonth = parseYearMonth(year, month);

        MonthlyBudgetEntity entity = budgetRepository.findByYearMonth(yearMonth)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No budget set for " + yearMonth));
        return toResponse(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MonthlyBudgetResponse create(
            @PathVariable int year,
            @PathVariable int month,
            @Valid @RequestBody MonthlyBudgetRequest request) {

        YearMonth yearMonth = parseYearMonth(year, month);

        if (budgetRepository.findByYearMonth(yearMonth).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Budget already set for " + yearMonth);
        }

        MonthlyBudgetEntity entity = new MonthlyBudgetEntity(
                null, yearMonth, request.income(), request.fixedExpenses(), request.savingsGoal());

        try {
            return toResponse(budgetRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            // A concurrent request may have created the budget between the findByYearMonth
            // check above and this save; the unique constraint on year_month is the real
            // guard, so translate its violation into the same 409 the check-based path returns.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Budget already set for " + yearMonth, e);
        }
    }

    private MonthlyBudgetResponse toResponse(MonthlyBudgetEntity monthlyBudgetEntity) {
        var available = monthlyBudgetEntity.getIncome().subtract(monthlyBudgetEntity.getFixedExpenses()).subtract(monthlyBudgetEntity.getSavingsGoal());
        return new MonthlyBudgetResponse(
                monthlyBudgetEntity.getId(), monthlyBudgetEntity.getYearMonth(), monthlyBudgetEntity.getIncome(), monthlyBudgetEntity.getFixedExpenses(), monthlyBudgetEntity.getSavingsGoal(), available);
    }

    private static YearMonth parseYearMonth(int year, int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid year/month: " + year + "/" + month, e);
        }
    }
}