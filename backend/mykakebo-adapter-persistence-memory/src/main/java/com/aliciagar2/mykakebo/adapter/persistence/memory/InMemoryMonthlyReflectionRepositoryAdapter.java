package com.aliciagar2.mykakebo.adapter.persistence.memory;

import com.aliciagar2.mykakebo.application.port.MonthlyReflectionRepositoryPort;
import com.aliciagar2.mykakebo.domain.MonthlyReflection;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMonthlyReflectionRepositoryAdapter implements MonthlyReflectionRepositoryPort {

    private final Map<YearMonth, MonthlyReflection> reflections = new ConcurrentSkipListMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    @Override
    public Optional<MonthlyReflection> findByYearMonth(YearMonth yearMonth) {
        return Optional.ofNullable(reflections.get(yearMonth));
    }

    @Override
    public List<MonthlyReflection> findAllByOrderByYearMonthAsc() {
        return List.copyOf(reflections.values());
    }

    @Override
    public MonthlyReflection save(MonthlyReflection reflection) {
        Long id = reflection.id() != null ? reflection.id() : idSequence.incrementAndGet();
        MonthlyReflection saved = new MonthlyReflection(id, reflection.user(), reflection.monthYear(),
                reflection.moneyHad(), reflection.moneySaved(), reflection.moneySpent(),
                reflection.improvementNote());
        reflections.put(reflection.monthYear(), saved);
        return saved;
    }
}
