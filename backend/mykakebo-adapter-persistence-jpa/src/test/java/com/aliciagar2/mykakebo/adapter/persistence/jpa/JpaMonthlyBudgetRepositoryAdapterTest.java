package com.aliciagar2.mykakebo.adapter.persistence.jpa;

import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.MonthlyBudgetEntity;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetAlreadyExistsException;
import com.aliciagar2.mykakebo.repository.MonthlyBudgetRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaMonthlyBudgetRepositoryAdapterTest {

    private static final YearMonth JANUARY = YearMonth.of(2024, 1);

    @Mock
    private MonthlyBudgetRepository monthlyBudgetRepository;

    @Mock
    private MonthlyBudgetEntityMapper mapper;

    @InjectMocks
    private JpaMonthlyBudgetRepositoryAdapter adapter;

    private static MonthlyBudget budget(Long id) {
        return new MonthlyBudget(id, null, JANUARY,
                new BigDecimal("3000.00"), new BigDecimal("1500.00"), new BigDecimal("500.00"));
    }

    private static MonthlyBudgetEntity entity(Long id) {
        return new MonthlyBudgetEntity(id, JANUARY,
                new BigDecimal("3000.00"), new BigDecimal("1500.00"), new BigDecimal("500.00"));
    }

    @Test
    void findByYearMonthReturnsEmptyWhenNotFound() {
        when(monthlyBudgetRepository.findByYearMonth(JANUARY)).thenReturn(Optional.empty());

        assertThat(adapter.findByYearMonth(JANUARY)).isEmpty();
    }

    @Test
    void findByYearMonthReturnsMappedBudgetWhenFound() {
        MonthlyBudgetEntity entity = entity(1L);
        MonthlyBudget budget = budget(1L);
        when(monthlyBudgetRepository.findByYearMonth(JANUARY)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(budget);

        assertThat(adapter.findByYearMonth(JANUARY)).contains(budget);
    }

    @Test
    void saveAbortsWhenUniqueConstraintFails() {
        MonthlyBudget newBudget = budget(null);
        MonthlyBudgetEntity newEntity = entity(null);
        DataIntegrityViolationException violation = new DataIntegrityViolationException("duplicate key");
        when(mapper.toNewEntity(newBudget)).thenReturn(newEntity);
        when(monthlyBudgetRepository.save(newEntity)).thenThrow(violation);

        assertThatThrownBy(() -> adapter.save(newBudget))
                .isInstanceOf(MonthlyBudgetAlreadyExistsException.class)
                .hasCause(violation);
    }

    @Test
    void savePersistsNewEntityAndReturnsMappedBudget() {
        MonthlyBudget newBudget = budget(null);
        MonthlyBudgetEntity newEntity = entity(null);
        MonthlyBudgetEntity savedEntity = entity(1L);
        MonthlyBudget savedBudget = budget(1L);
        when(mapper.toNewEntity(newBudget)).thenReturn(newEntity);
        when(monthlyBudgetRepository.save(newEntity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedBudget);

        assertThat(adapter.save(newBudget)).isEqualTo(savedBudget);
    }
}
