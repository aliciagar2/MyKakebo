package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.domain.MonthlyReflection;
import com.aliciagar2.mykakebo.domain.MonthlyReflectionEntity;
import com.aliciagar2.mykakebo.repository.MonthlyReflectionRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaMonthlyReflectionRepositoryAdapterTest {

    private static final YearMonth JANUARY = YearMonth.of(2024, 1);

    @Mock
    private MonthlyReflectionRepository monthlyReflectionRepository;

    @Mock
    private MonthlyReflectionEntityMapper mapper;

    @InjectMocks
    private JpaMonthlyReflectionRepositoryAdapter adapter;

    private static MonthlyReflectionEntity entity(Long id) {
        return new MonthlyReflectionEntity(id, JANUARY,
                new BigDecimal("3000.00"), new BigDecimal("400.00"), new BigDecimal("1100.00"), "note");
    }

    @Test
    void findAllByOrderByYearMonthAscMapsEntitiesToDomain() {
        MonthlyReflectionEntity entity = entity(1L);
        MonthlyReflection reflection = reflection(1L);
        when(monthlyReflectionRepository.findAllByOrderByYearMonthAsc()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(reflection);

        assertThat(adapter.findAllByOrderByYearMonthAsc()).containsExactly(reflection);
    }

    @Test
    void findByYearMonthReturnsEmptyWhenNotFound() {
        when(monthlyReflectionRepository.findByYearMonth(JANUARY)).thenReturn(Optional.empty());

        assertThat(adapter.findByYearMonth(JANUARY)).isEmpty();
    }

    @Test
    void findByYearMonthReturnsMappedReflectionWhenFound() {
        MonthlyReflectionEntity entity = entity(1L);
        MonthlyReflection reflection = reflection(1L);
        when(monthlyReflectionRepository.findByYearMonth(JANUARY)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(reflection);

        assertThat(adapter.findByYearMonth(JANUARY)).contains(reflection);
    }

    private static MonthlyReflection reflection(Long id) {
        return new MonthlyReflection(id, null, JANUARY,
                new BigDecimal("3000.00"), new BigDecimal("400.00"), new BigDecimal("1100.00"), "note");
    }

    @Test
    void savePersistsEntityAndReturnsMappedReflection() {
        MonthlyReflection newReflection = reflection(null);
        MonthlyReflectionEntity newEntity = entity(null);
        MonthlyReflectionEntity savedEntity = entity(1L);
        MonthlyReflection savedReflection = reflection(1L);
        when(mapper.toEntity(newReflection)).thenReturn(newEntity);
        when(monthlyReflectionRepository.save(newEntity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedReflection);

        assertThat(adapter.save(newReflection)).isEqualTo(savedReflection);
    }
}
