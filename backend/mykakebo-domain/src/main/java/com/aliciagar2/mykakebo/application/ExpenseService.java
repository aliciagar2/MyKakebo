package com.aliciagar2.mykakebo.application;

import com.aliciagar2.mykakebo.application.port.ExpenseRepositoryPort;
import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseConcurrentlyModifiedException;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseDateOutsideMonthException;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.YearMonthInvalidException;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class ExpenseService {

    private final ExpenseRepositoryPort expenseRepositoryPort;

    public ExpenseService(ExpenseRepositoryPort expenseRepositoryPort) {
        this.expenseRepositoryPort = expenseRepositoryPort;
    }

    public List<Expense> list(int year, int month, KakeboCategory category) {
        YearMonth ym = parseYearMonth(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return (category == null)
                ? expenseRepositoryPort.findByDateRange(start, end)
                : expenseRepositoryPort.findByDateRangeAndCategory(start, end, category);
    }

    public Expense create(int year, int month, KakeboCategory category, BigDecimal amount, LocalDate date, String note) {
        YearMonth ym = parseYearMonth(year, month);
        requireDateInMonth(date, ym);

        Expense expense = new Expense(null, null, ym, category, amount, date, note);
        return expenseRepositoryPort.save(expense);
    }

    public Expense update(int year, int month, Long id, KakeboCategory category, BigDecimal amount, LocalDate date, String note) {
        YearMonth ym = parseYearMonth(year, month);
        requireDateInMonth(date, ym);

        Expense existing = expenseRepositoryPort.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found: " + id));

        Expense updated = new Expense(existing.id(), existing.user(), ym, category, amount, date, note);

        try {
            return expenseRepositoryPort.save(updated);
        } catch (ExpenseConcurrentlyModifiedException e) {
            // Another request deleted this expense between the findById above and this save;
            // surface it the same way as the findById miss above.
            throw new ExpenseNotFoundException("Expense not found: " + id, e);
        }
    }

    public void delete(Long id) {
        if (!expenseRepositoryPort.existsById(id)) {
            throw new ExpenseNotFoundException("Expense not found: " + id);
        }
        expenseRepositoryPort.deleteById(id);
    }

    private static YearMonth parseYearMonth(int year, int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException e) {
            throw new YearMonthInvalidException("Invalid year/month: " + year + "/" + month, e);
        }
    }

    private static void requireDateInMonth(LocalDate date, YearMonth yearMonth) {
        if (!YearMonth.from(date).equals(yearMonth)) {
            throw new ExpenseDateOutsideMonthException("date " + date + " is not in " + yearMonth);
        }
    }
}
