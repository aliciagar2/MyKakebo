package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.domain.exceptions.ExpenseNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetAlreadyExistsException;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetNotFoundException;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalExceptionHandlerTest.ThrowingController.class)
@Import(GlobalExceptionHandlerTest.ThrowingController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void expenseNotFoundReturns404WithErrorBody() throws Exception {
        mockMvc.perform(get("/test/expense-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXPENSE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Expense not found"));
    }

    @Test
    void monthlyBudgetAlreadyExistsReturns409() throws Exception {
        mockMvc.perform(get("/test/budget-exists"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("MONTHLY_BUDGET_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Monthly budget already exists"));
    }

    @Test
    void monthlyBudgetNotFoundReturns404() throws Exception {
        mockMvc.perform(get("/test/budget-not-exists"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MONTHLY_BUDGET_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Monthly budget not found"));
    }

    @RestController
    @RequestMapping("/test")
    static class ThrowingController {
        @GetMapping("/expense-not-found")
        void expenseNotFound() {
            throw new ExpenseNotFoundException("Expense not found");
        }

        @GetMapping("/budget-exists")
        void budgetExists() {
            throw new MonthlyBudgetAlreadyExistsException("Budget already exists");
        }

        @GetMapping("/budget-not-exists")
        void budgetNotExists() {
            throw new MonthlyBudgetNotFoundException("Budget not found");
        }

        record SampleBody(@NotBlank String name) {
        }
    }
}