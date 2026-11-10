package com.aliciagar2.mykakebo.application;

import com.aliciagar2.mykakebo.application.port.MonthlyReflectionRepositoryPort;
import com.aliciagar2.mykakebo.domain.MonthlyReflection;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyReflectionNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.YearMonthInvalidException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.List;

public class MonthlyReflectionService {

    private final MonthlyReflectionRepositoryPort monthlyReflectionRepositoryPort;

    public MonthlyReflectionService(MonthlyReflectionRepositoryPort monthlyReflectionRepositoryPort) {
        this.monthlyReflectionRepositoryPort = monthlyReflectionRepositoryPort;
    }

    public MonthlyReflection get(int year, int month) {
        YearMonth yearMonth = parseYearMonth(year, month);
        return monthlyReflectionRepositoryPort.findByYearMonth(yearMonth)
                .orElseThrow(() -> new MonthlyReflectionNotFoundException("No reflection for " + yearMonth));
    }

    public List<MonthlyReflection> list() {
        return monthlyReflectionRepositoryPort.findAllByOrderByYearMonthAsc();
    }

    public MonthlyReflection save(MonthlyReflection reflection) {
        return monthlyReflectionRepositoryPort.save(reflection);
    }

    private static YearMonth parseYearMonth(int year, int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException e) {
            throw new YearMonthInvalidException("Invalid year/month: " + year + "/" + month, e);
        }
    }
}
