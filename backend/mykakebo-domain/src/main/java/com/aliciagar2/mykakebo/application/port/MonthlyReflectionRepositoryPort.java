package com.aliciagar2.mykakebo.application.port;

import com.aliciagar2.mykakebo.domain.MonthlyReflection;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface MonthlyReflectionRepositoryPort {

    Optional<MonthlyReflection> findByYearMonth(YearMonth yearMonth);

    List<MonthlyReflection> findAllByOrderByYearMonthAsc();

    MonthlyReflection save(MonthlyReflection reflection);
}
