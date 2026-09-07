package com.aliciagar2.mykakebo.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KakeboCategory category;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    private String note;

    protected ExpenseEntity() {
    }

    public ExpenseEntity(Long id, KakeboCategory category, BigDecimal amount, LocalDate date, String note) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KakeboCategory getCategory() {
        return category;
    }

    public void setCategory(KakeboCategory category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
