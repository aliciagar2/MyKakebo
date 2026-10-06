package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.domain.ExpenseEntity;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExpenseRepository expenseRepository;

    private static ExpenseEntity expense(Long id, KakeboCategory category, String amount, LocalDate date, String note) {
        return new ExpenseEntity(id, category, new BigDecimal(amount), date, note);
    }

    @Test
    void listReturnsExpensesInMonth() throws Exception {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        when(expenseRepository.findByExpenseDateBetween(start, end)).thenReturn(List.of(
                expense(1L, KakeboCategory.SURVIVAL, "50.00", LocalDate.of(2026, 1, 15), "Groceries")));

        mockMvc.perform(get("/api/months/2026/1/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].category").value("SURVIVAL"))
                .andExpect(jsonPath("$[0].note").value("Groceries"));
    }

    @Test
    void listFiltersByCategoryWhenProvided() throws Exception {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        when(expenseRepository.findByExpenseDateBetweenAndCategory(start, end, KakeboCategory.OPTIONAL))
                .thenReturn(List.of(expense(2L, KakeboCategory.OPTIONAL, "30.00", LocalDate.of(2026, 1, 20), "Dining")));

        mockMvc.perform(get("/api/months/2026/1/expenses").param("category", "OPTIONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("OPTIONAL"));

        verify(expenseRepository).findByExpenseDateBetweenAndCategory(start, end, KakeboCategory.OPTIONAL);
        verify(expenseRepository, never()).findByExpenseDateBetween(any(), any());
    }

    @Test
    void createPersistsExpenseAndReturns201() throws Exception {
        when(expenseRepository.save(any(ExpenseEntity.class))).thenAnswer(invocation -> {
            ExpenseEntity e = invocation.getArgument(0);
            return expense(10L, e.getCategory(), e.getAmount().toPlainString(), e.getExpenseDate(), e.getNote());
        });

        String body = """
                {"category":"SURVIVAL","amount":42.50,"date":"2026-01-10","note":"Rent"}
                """;

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.category").value("SURVIVAL"))
                .andExpect(jsonPath("$.note").value("Rent"));
    }

    @Test
    void updateModifiesExistingExpense() throws Exception {
        ExpenseEntity existing = expense(5L, KakeboCategory.SURVIVAL, "50.00", LocalDate.of(2026, 1, 15), "Groceries");
        when(expenseRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(expenseRepository.save(any(ExpenseEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String body = """
                {"category":"OPTIONAL","amount":99.99,"date":"2026-01-16","note":"Updated"}
                """;

        mockMvc.perform(put("/api/months/2026/1/expenses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.category").value("OPTIONAL"))
                .andExpect(jsonPath("$.note").value("Updated"));
    }

    @Test
    void deleteRemovesExistingExpense() throws Exception {
        when(expenseRepository.existsById(5L)).thenReturn(true);

        mockMvc.perform(delete("/api/months/2026/1/expenses/5"))
                .andExpect(status().isNoContent());

        verify(expenseRepository).deleteById(5L);
    }

    @Test
    void listReturnsEmptyArrayWhenNoExpensesInMonth() throws Exception {
        LocalDate start = LocalDate.of(2026, 2, 1);
        LocalDate end = LocalDate.of(2026, 2, 28);
        when(expenseRepository.findByExpenseDateBetween(start, end)).thenReturn(List.of());

        mockMvc.perform(get("/api/months/2026/2/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createAcceptsSmallestPositiveAmount() throws Exception {
        when(expenseRepository.save(any(ExpenseEntity.class))).thenAnswer(invocation -> {
            ExpenseEntity e = invocation.getArgument(0);
            return expense(11L, e.getCategory(), e.getAmount().toPlainString(), e.getExpenseDate(), e.getNote());
        });

        String body = """
                {"category":"EXTRA","amount":0.01,"date":"2026-01-10","note":null}
                """;

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(0.01));
    }

    @Test
    void createAcceptsNoteAtMaxLength() throws Exception {
        String maxNote = "n".repeat(500);
        when(expenseRepository.save(any(ExpenseEntity.class))).thenAnswer(invocation -> {
            ExpenseEntity e = invocation.getArgument(0);
            return expense(12L, e.getCategory(), e.getAmount().toPlainString(), e.getExpenseDate(), e.getNote());
        });

        String body = objectMapper.writeValueAsString(new TestExpenseRequest(KakeboCategory.CULTURE, new BigDecimal("5.00"), LocalDate.of(2026, 1, 1), maxNote));

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.note").value(maxNote));
    }

    @Test
    void updateReturns404WhenExpenseDoesNotExist() throws Exception {
        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());

        String body = """
                {"category":"SURVIVAL","amount":10.00,"date":"2026-01-01","note":null}
                """;

        mockMvc.perform(put("/api/months/2026/1/expenses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());

        verify(expenseRepository, never()).save(any());
    }

    @Test
    void deleteReturns404WhenExpenseDoesNotExist() throws Exception {
        when(expenseRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/months/2026/1/expenses/999"))
                .andExpect(status().isNotFound());

        verify(expenseRepository, never()).deleteById(any());
    }

    @Test
    void listIgnoresUnknownCategoryValueAsBadRequest() throws Exception {
        mockMvc.perform(get("/api/months/2026/1/expenses").param("category", "NOT_A_CATEGORY"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturns400ForInvalidMonth() throws Exception {
        mockMvc.perform(get("/api/months/2026/13/expenses"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(expenseRepository);
    }

    @Test
    void listReturns400ForZeroMonth() throws Exception {
        mockMvc.perform(get("/api/months/2026/0/expenses"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(expenseRepository);
    }


    @Test
    void createRejectsMissingCategory() throws Exception {
        String body = """
                {"amount":10.00,"date":"2026-01-01","note":null}
                """;

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(expenseRepository);
    }

    @Test
    void createRejectsZeroAmount() throws Exception {
        String body = """
                {"category":"SURVIVAL","amount":0,"date":"2026-01-01","note":null}
                """;

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(expenseRepository);
    }

    @Test
    void createRejectsNegativeAmount() throws Exception {
        String body = """
                {"category":"SURVIVAL","amount":-5.00,"date":"2026-01-01","note":null}
                """;

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRejectsMissingDate() throws Exception {
        String body = """
                {"category":"SURVIVAL","amount":10.00,"note":null}
                """;

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRejectsNoteOverMaxLength() throws Exception {
        String tooLongNote = "n".repeat(501);
        String body = objectMapper.writeValueAsString(new TestExpenseRequest(KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2026, 1, 1), tooLongNote));

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRejectsMalformedJsonBody() throws Exception {
        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not valid json"))
                .andExpect(status().isBadRequest());
    }

    private record TestExpenseRequest(KakeboCategory category, BigDecimal amount, LocalDate date, String note) {
    }
}
