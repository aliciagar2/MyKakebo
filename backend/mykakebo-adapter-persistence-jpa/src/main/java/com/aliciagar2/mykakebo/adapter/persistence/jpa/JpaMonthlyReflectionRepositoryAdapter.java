package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.application.port.MonthlyReflectionRepositoryPort;
import com.aliciagar2.mykakebo.domain.MonthlyReflection;
import com.aliciagar2.mykakebo.repository.MonthlyReflectionRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaMonthlyReflectionRepositoryAdapter implements MonthlyReflectionRepositoryPort {

    private final MonthlyReflectionRepository monthlyReflectionRepository;
    private final MonthlyReflectionEntityMapper mapper;

    public JpaMonthlyReflectionRepositoryAdapter(MonthlyReflectionRepository monthlyReflectionRepository,
                                                 MonthlyReflectionEntityMapper mapper) {
        this.monthlyReflectionRepository = monthlyReflectionRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<MonthlyReflection> findByYearMonth(YearMonth yearMonth) {
        return monthlyReflectionRepository.findByYearMonth(yearMonth).map(mapper::toDomain);
    }

    @Override
    public List<MonthlyReflection> findAllByOrderByYearMonthAsc() {
        return monthlyReflectionRepository.findAllByOrderByYearMonthAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public MonthlyReflection save(MonthlyReflection reflection) {
        return mapper.toDomain(monthlyReflectionRepository.save(mapper.toEntity(reflection)));
    }
}
