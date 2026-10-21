package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.ExpenseEntity;
import java.time.YearMonth;
import org.springframework.stereotype.Component;

@Component
public class ExpenseEntityMapper {

    // ExpenseEntity has no user/monthYear columns: monthYear is derived from the expense date,
    // and user is left null, preserving the existing persisted shape without inventing new columns.
    public Expense toDomain(ExpenseEntity entity) {
        return new Expense(
                entity.getId(),
                null,
                YearMonth.from(entity.getExpenseDate()),
                entity.getCategory(),
                entity.getAmount(),
                entity.getExpenseDate(),
                entity.getNote());
    }

    public ExpenseEntity toNewEntity(Expense expense) {
        return new ExpenseEntity(null, expense.category(), expense.amount(), expense.date(), expense.note());
    }

    public void applyTo(Expense expense, ExpenseEntity entity) {
        entity.setCategory(expense.category());
        entity.setAmount(expense.amount());
        entity.setExpenseDate(expense.date());
        entity.setNote(expense.note());
    }
}
