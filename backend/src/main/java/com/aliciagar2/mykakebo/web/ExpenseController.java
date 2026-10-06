package com.aliciagar2.mykakebo.web;


import com.aliciagar2.mykakebo.domain.ExpenseEntity;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.dto.ExpenseRequest;
import com.aliciagar2.mykakebo.dto.ExpenseResponse;
import com.aliciagar2.mykakebo.repository.ExpenseRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/months/{year}/{month}/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping
    public List<ExpenseResponse> list(
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam(required = false) KakeboCategory category) {

        YearMonth ym = parseYearMonth(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<ExpenseEntity> expenses = (category == null)
                ? expenseRepository.findByExpenseDateBetween(start, end)
                : expenseRepository.findByExpenseDateBetweenAndCategory(start, end, category);

        return expenses.stream().map(this::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(
            @PathVariable int year,
            @PathVariable int month,
            @Valid @RequestBody ExpenseRequest request) {

        ExpenseEntity entity = new ExpenseEntity(
                null, request.category(), request.amount(), request.date(), request.note());

        return toResponse(expenseRepository.save(entity));
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found: " + id));

        entity.setCategory(request.category());
        entity.setAmount(request.amount());
        entity.setExpenseDate(request.date());
        entity.setNote(request.note());

        return toResponse(expenseRepository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found: " + id);
        }
        expenseRepository.deleteById(id);
    }

    private ExpenseResponse toResponse(ExpenseEntity e) {
        return new ExpenseResponse(e.getId(), e.getCategory(), e.getAmount(), e.getExpenseDate(), e.getNote());
    }

    private static YearMonth parseYearMonth(int year, int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid year/month: " + year + "/" + month, e);
        }
    }
}