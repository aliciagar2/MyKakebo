package com.aliciagar2.mykakebo.repository;

import com.aliciagar2.mykakebo.domain.MonthlyBudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Optional;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudgetEntity, Long> {

    Optional<MonthlyBudgetEntity> findByYearMonth(YearMonth yearMonth);
}