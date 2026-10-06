package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.domain.MonthlyBudgetEntity;
import com.aliciagar2.mykakebo.repository.MonthlyBudgetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MonthlyBudgetController.class)
class MonthlyBudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MonthlyBudgetRepository monthlyBudgetRepository;

    private static MonthlyBudgetEntity budget(Long id, YearMonth ym, String income, String fixedExpenses, String savingsGoal) {
        return new MonthlyBudgetEntity(id, ym, new BigDecimal(income), new BigDecimal(fixedExpenses), new BigDecimal(savingsGoal));
    }

    @Test
    void getReturnsBudgetWithCalculatedAvailableToSpend() throws Exception {
        YearMonth ym = YearMonth.of(2026, 1);
        when(monthlyBudgetRepository.findByYearMonth(ym))
                .thenReturn(Optional.of(budget(1L, ym, "3000.00", "1500.00", "500.00")));

        mockMvc.perform(get("/api/months/2026/1/budget"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.income").value(3000.00))
                .andExpect(jsonPath("$.fixedExpenses").value(1500.00))
                .andExpect(jsonPath("$.savingsGoal").value(500.00))
                .andExpect(jsonPath("$.availableToSpend").value(1000.00));
    }

    @Test
    void createPersistsBudgetAndReturns201() throws Exception {
        YearMonth ym = YearMonth.of(2026, 3);
        when(monthlyBudgetRepository.findByYearMonth(ym)).thenReturn(Optional.empty());
        when(monthlyBudgetRepository.save(any(MonthlyBudgetEntity.class))).thenAnswer(invocation -> {
            MonthlyBudgetEntity e = invocation.getArgument(0);
            return budget(7L, e.getYearMonth(), e.getIncome().toPlainString(),
                    e.getFixedExpenses().toPlainString(), e.getSavingsGoal().toPlainString());
        });

        String body = """
                {"income":2000.00,"fixedExpenses":800.00,"savingsGoal":200.00}
                """;

        mockMvc.perform(post("/api/months/2026/3/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.availableToSpend").value(1000.00));
    }

    @Test
    void createAcceptsAllZeroAmounts() throws Exception {
        YearMonth ym = YearMonth.of(2026, 4);
        when(monthlyBudgetRepository.findByYearMonth(ym)).thenReturn(Optional.empty());
        when(monthlyBudgetRepository.save(any(MonthlyBudgetEntity.class))).thenAnswer(invocation -> {
            MonthlyBudgetEntity e = invocation.getArgument(0);
            return budget(8L, e.getYearMonth(), e.getIncome().toPlainString(),
                    e.getFixedExpenses().toPlainString(), e.getSavingsGoal().toPlainString());
        });

        String body = """
                {"income":0,"fixedExpenses":0,"savingsGoal":0}
                """;

        mockMvc.perform(post("/api/months/2026/4/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.availableToSpend").value(0));
    }

    @Test
    void createAllowsAvailableToSpendToGoNegativeWhenExpensesExceedIncome() throws Exception {
        YearMonth ym = YearMonth.of(2026, 5);
        when(monthlyBudgetRepository.findByYearMonth(ym)).thenReturn(Optional.empty());
        when(monthlyBudgetRepository.save(any(MonthlyBudgetEntity.class))).thenAnswer(invocation -> {
            MonthlyBudgetEntity e = invocation.getArgument(0);
            return budget(9L, e.getYearMonth(), e.getIncome().toPlainString(),
                    e.getFixedExpenses().toPlainString(), e.getSavingsGoal().toPlainString());
        });

        String body = """
                {"income":100.00,"fixedExpenses":80.00,"savingsGoal":50.00}
                """;

        mockMvc.perform(post("/api/months/2026/5/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.availableToSpend").value(-30.00));
    }

    @Test
    void getReturns404WhenNoBudgetSetForMonth() throws Exception {
        when(monthlyBudgetRepository.findByYearMonth(YearMonth.of(2026, 6))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/months/2026/6/budget"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturns409WhenBudgetAlreadyExistsForMonth() throws Exception {
        YearMonth ym = YearMonth.of(2026, 1);
        when(monthlyBudgetRepository.findByYearMonth(ym))
                .thenReturn(Optional.of(budget(1L, ym, "3000.00", "1500.00", "500.00")));

        String body = """
                {"income":1000.00,"fixedExpenses":200.00,"savingsGoal":100.00}
                """;

        mockMvc.perform(post("/api/months/2026/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());

        verify(monthlyBudgetRepository, never()).save(any());
    }

    @Test
    void createRejectsMissingIncome() throws Exception {
        String body = """
                {"fixedExpenses":100.00,"savingsGoal":50.00}
                """;

        mockMvc.perform(post("/api/months/2026/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(monthlyBudgetRepository);
    }

    @Test
    void createRejectsNegativeFixedExpenses() throws Exception {
        String body = """
                {"income":1000.00,"fixedExpenses":-1.00,"savingsGoal":50.00}
                """;

        mockMvc.perform(post("/api/months/2026/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRejectsNegativeSavingsGoal() throws Exception {
        String body = """
                {"income":1000.00,"fixedExpenses":100.00,"savingsGoal":-50.00}
                """;

        mockMvc.perform(post("/api/months/2026/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRejectsMalformedJsonBody() throws Exception {
        mockMvc.perform(post("/api/months/2026/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not valid json"))
                .andExpect(status().isBadRequest());
    }
}
