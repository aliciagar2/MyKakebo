package com.aliciagar2.mykakebo.repository;

import com.aliciagar2.mykakebo.domain.MonthlyReflectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface MonthlyReflectionRepository extends JpaRepository<MonthlyReflectionEntity, Long> {

    Optional<MonthlyReflectionEntity> findByYearMonth(YearMonth yearMonth);

    List<MonthlyReflectionEntity> findAllByOrderByYearMonthAsc();
}