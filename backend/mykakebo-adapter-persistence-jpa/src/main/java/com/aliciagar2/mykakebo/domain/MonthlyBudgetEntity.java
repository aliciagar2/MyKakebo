package com.aliciagar2.mykakebo.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "monthly_budgets", uniqueConstraints = @UniqueConstraint(columnNames = "year_month"))
public class MonthlyBudgetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_month", nullable = false)
    private YearMonth yearMonth;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal income;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal fixedExpenses;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal savingsGoal;

    protected MonthlyBudgetEntity() {}

    public MonthlyBudgetEntity(Long id, YearMonth yearMonth, BigDecimal income, BigDecimal fixedExpenses, BigDecimal savingsGoal) {
        this.id = id;
        this.yearMonth = yearMonth;
        this.income = income;
        this.fixedExpenses = fixedExpenses;
        this.savingsGoal = savingsGoal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getFixedExpenses() {
        return fixedExpenses;
    }

    public void setFixedExpenses(BigDecimal fixedExpenses) {
        this.fixedExpenses = fixedExpenses;
    }

    public BigDecimal getSavingsGoal() {
        return savingsGoal;
    }

    public void setSavingsGoal(BigDecimal savingsGoal) {
        this.savingsGoal = savingsGoal;
    }
}
