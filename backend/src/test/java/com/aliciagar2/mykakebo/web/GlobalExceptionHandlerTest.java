package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.domain.exceptions.ExpenseNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetAlreadyExistsException;
import com.aliciagar2.mykakebo.domain.exceptions.MonthlyBudgetNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void invalidRequestBodyReturns400WithFieldViolations() throws Exception {
        mockMvc.perform(post("/test/validated")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.violations[0].field").value("name"))
                .andExpect(jsonPath("$.violations[0].message").value("must not be blank"));
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

    @Test
    void unexpectedExceptionReturns500WithoutLeakingDetails() throws Exception {
        mockMvc.perform(get("/test/crash"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Something wrong happened"))
                .andExpect(content().string(not(containsString("secret detail"))));
    }

    @Test
    void methodNotAllowedReturns405WithoutLeakingDetails() throws Exception {
        mockMvc.perform(post("/test/expense-not-found"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("REQUEST_ERROR"))
                .andExpect(jsonPath("$.message").value("The request could not be processed"))
                .andExpect(content().string(not(containsString("not supported"))));
    }

    @RestController
    @RequestMapping("/test")
    static class ThrowingController {
        @GetMapping("/budget-exists")
        void budgetExists() {
            throw new MonthlyBudgetAlreadyExistsException("Budget already exists");
        }

        @GetMapping("/budget-not-exists")
        void budgetNotExists() {
            throw new MonthlyBudgetNotFoundException("Budget not found");
        }

        @GetMapping("/crash")
        void crash() {
            throw new IllegalStateException();
        }

        @GetMapping("/expense-not-found")
        void expenseNotFound() {
            throw new ExpenseNotFoundException("Expense not found");
        }

        @PostMapping("/validated")
        void validated(@Valid @RequestBody SampleBody body) {
        }

        record SampleBody(@NotBlank String name) {
        }
    }
}
