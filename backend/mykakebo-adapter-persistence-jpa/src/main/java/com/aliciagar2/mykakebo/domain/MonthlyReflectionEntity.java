package com.aliciagar2.mykakebo.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "monthly_reflections")
public class MonthlyReflectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_month", nullable = false)
    private YearMonth yearMonth;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal moneyHad;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal moneySaved;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal moneySpent;

    @Column(columnDefinition = "TEXT")
    private String improvementNote;

    protected MonthlyReflectionEntity() {
    }

    public MonthlyReflectionEntity(Long id, YearMonth yearMonth, BigDecimal moneyHad, BigDecimal moneySaved, BigDecimal moneySpent, String improvementNote) {
        this.id = id;
        this.yearMonth = yearMonth;
        this.moneyHad = moneyHad;
        this.moneySaved = moneySaved;
        this.moneySpent = moneySpent;
        this.improvementNote = improvementNote;
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

    public BigDecimal getMoneyHad() {
        return moneyHad;
    }

    public void setMoneyHad(BigDecimal moneyHad) {
        this.moneyHad = moneyHad;
    }

    public BigDecimal getMoneySaved() {
        return moneySaved;
    }

    public void setMoneySaved(BigDecimal moneySaved) {
        this.moneySaved = moneySaved;
    }

    public BigDecimal getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(BigDecimal moneySpent) {
        this.moneySpent = moneySpent;
    }

    public String getImprovementNote() {
        return improvementNote;
    }

    public void setImprovementNote(String improvementNote) {
        this.improvementNote = improvementNote;
    }
}
