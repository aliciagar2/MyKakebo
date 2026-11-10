package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.application.MonthlyBudgetService;
import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.dto.MonthlyBudgetRequest;
import com.aliciagar2.mykakebo.dto.MonthlyBudgetResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/months/{year}/{month}/budget")
public class MonthlyBudgetController {

    private final MonthlyBudgetService monthlyBudgetService;

    public MonthlyBudgetController(MonthlyBudgetService monthlyBudgetService) {
        this.monthlyBudgetService = monthlyBudgetService;
    }

    @GetMapping
    public MonthlyBudgetResponse get(@PathVariable int year, @PathVariable int month) {
        return toResponse(monthlyBudgetService.get(year, month));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MonthlyBudgetResponse create(
            @PathVariable int year,
            @PathVariable int month,
            @Valid @RequestBody MonthlyBudgetRequest request) {
        return toResponse(monthlyBudgetService.create(
                year, month, request.income(), request.fixedExpenses(), request.savingsGoal()));
    }

    private MonthlyBudgetResponse toResponse(MonthlyBudget budget) {
        return new MonthlyBudgetResponse(
                budget.id(), budget.monthYear(), budget.income(), budget.fixedExpenses(), budget.savingsGoal(),
                budget.availableToSpend());
    }
}
