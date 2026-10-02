package com.aliciagar2.mykakebo.repository;

import com.aliciagar2.mykakebo.domain.ExpenseEntity;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByExpenseDateBetween(LocalDate start, LocalDate end);

    List<ExpenseEntity> findByExpenseDateBetweenAndCategory(LocalDate start, LocalDate end, KakeboCategory category);
}