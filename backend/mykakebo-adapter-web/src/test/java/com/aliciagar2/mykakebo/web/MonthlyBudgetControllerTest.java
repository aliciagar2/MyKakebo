package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.application.MonthlyBudgetService;
import com.aliciagar2.mykakebo.domain.MonthlyBudget;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetAlreadyExistsException;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.YearMonthInvalidException;
import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MonthlyBudgetController.class)
class MonthlyBudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MonthlyBudgetService monthlyBudgetService;

    @Test
    void createAcceptsAllZeroAmounts() throws Exception {
        when(monthlyBudgetService.create(eq(2026), eq(4), any(), any(), any())).thenAnswer(invocation ->
                new MonthlyBudget(8L, null, YearMonth.of(2026, 4), invocation.getArgument(2),
                        invocation.getArgument(3), invocation.getArgument(4)));

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
        when(monthlyBudgetService.create(eq(2026), eq(5), any(), any(), any())).thenAnswer(invocation ->
                new MonthlyBudget(9L, null, YearMonth.of(2026, 5), invocation.getArgument(2),
                        invocation.getArgument(3), invocation.getArgument(4)));

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
    void createPersistsBudgetAndReturns201() throws Exception {
        when(monthlyBudgetService.create(eq(2026), eq(3), any(), any(), any())).thenAnswer(invocation ->
                new MonthlyBudget(7L, null, YearMonth.of(2026, 3), invocation.getArgument(2),
                        invocation.getArgument(3), invocation.getArgument(4)));

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
    void createRejectsIncomeWithTooManyDecimalPlaces() throws Exception {
        String body = """
                {"income":1000.001,"fixedExpenses":100.00,"savingsGoal":50.00}
                """;

        mockMvc.perform(post("/api/months/2026/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(monthlyBudgetService);
    }

    @Test
    void createRejectsMalformedJsonBody() throws Exception {
        mockMvc.perform(post("/api/months/2026/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not valid json"))
                .andExpect(status().isBadRequest());
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

        verifyNoInteractions(monthlyBudgetService);
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
    void createReturns400ForInvalidMonth() throws Exception {
        // The year/month rule itself is unit-tested in MonthlyBudgetServiceTest; this proves the 400 mapping.
        when(monthlyBudgetService.create(anyInt(), anyInt(), any(), any(), any()))
                .thenThrow(new YearMonthInvalidException("Invalid year/month: 2026/0"));

        String body = """
                {"income":1000.00,"fixedExpenses":200.00,"savingsGoal":100.00}
                """;

        mockMvc.perform(post("/api/months/2026/0/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReturns409WhenBudgetAlreadyExistsForMonth() throws Exception {
        when(monthlyBudgetService.create(anyInt(), anyInt(), any(), any(), any()))
                .thenThrow(new MonthlyBudgetAlreadyExistsException("Budget already set for 2026-01"));

        String body = """
                {"income":1000.00,"fixedExpenses":200.00,"savingsGoal":100.00}
                """;

        mockMvc.perform(post("/api/months/2026/1/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void getReturns400ForInvalidMonth() throws Exception {
        when(monthlyBudgetService.get(2026, 13))
                .thenThrow(new YearMonthInvalidException("Invalid year/month: 2026/13"));

        mockMvc.perform(get("/api/months/2026/13/budget"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReturns404WhenNoBudgetSetForMonth() throws Exception {
        when(monthlyBudgetService.get(2026, 6))
                .thenThrow(new MonthlyBudgetNotFoundException("No budget set for 2026-06"));

        mockMvc.perform(get("/api/months/2026/6/budget"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReturnsBudgetWithCalculatedAvailableToSpend() throws Exception {
        when(monthlyBudgetService.get(2026, 1)).thenReturn(new MonthlyBudget(1L, null, YearMonth.of(2026, 1),
                new BigDecimal("3000.00"), new BigDecimal("1500.00"), new BigDecimal("500.00")));

        mockMvc.perform(get("/api/months/2026/1/budget"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.income").value(3000.00))
                .andExpect(jsonPath("$.fixedExpenses").value(1500.00))
                .andExpect(jsonPath("$.savingsGoal").value(500.00))
                .andExpect(jsonPath("$.availableToSpend").value(1000.00));
    }
}
