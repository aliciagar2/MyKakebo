package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.application.ExpenseService;
import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.dto.ExpenseRequest;
import com.aliciagar2.mykakebo.dto.ExpenseResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/months/{year}/{month}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseResponse> list(
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam(required = false) KakeboCategory category) {

        return expenseService.list(year, month, category).stream().map(this::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(
            @PathVariable int year,
            @PathVariable int month,
            @Valid @RequestBody ExpenseRequest request) {

        Expense created = expenseService.create(
                year, month, request.category(), request.amount(), request.date(), request.note());
        return toResponse(created);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {

        Expense updated = expenseService.update(
                year, month, id, request.category(), request.amount(), request.date(), request.note());
        return toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        expenseService.delete(id);
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(expense.id(), expense.category(), expense.amount(), expense.date(), expense.note());
    }
}
