package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.domain.MonthlyBudgetEntity;
import com.aliciagar2.mykakebo.dto.MonthlyBudgetRequest;
import com.aliciagar2.mykakebo.dto.MonthlyBudgetResponse;
import com.aliciagar2.mykakebo.repository.MonthlyBudgetRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        YearMonth ym = YearMonth.of(year, month);
        MonthlyBudgetEntity entity = budgetRepository.findByYearMonth(ym)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No budget set for " + ym));
        return toResponse(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MonthlyBudgetResponse create(
            @PathVariable int year,
            @PathVariable int month,
            @Valid @RequestBody MonthlyBudgetRequest request) {

        YearMonth ym = YearMonth.of(year, month);

        if (budgetRepository.findByYearMonth(ym).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Budget already set for " + ym);
        }

        MonthlyBudgetEntity entity = new MonthlyBudgetEntity(
                null, ym, request.income(), request.fixedExpenses(), request.savingsGoal());

        return toResponse(budgetRepository.save(entity));
    }

    private MonthlyBudgetResponse toResponse(MonthlyBudgetEntity e) {
        var available = e.getIncome().subtract(e.getFixedExpenses()).subtract(e.getSavingsGoal());
        return new MonthlyBudgetResponse(
                e.getId(), e.getYearMonth(), e.getIncome(), e.getFixedExpenses(), e.getSavingsGoal(), available);
    }
}