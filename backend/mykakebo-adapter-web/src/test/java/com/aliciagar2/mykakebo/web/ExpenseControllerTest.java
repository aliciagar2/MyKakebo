package com.aliciagar2.mykakebo.web;

import com.aliciagar2.mykakebo.application.ExpenseService;
import com.aliciagar2.mykakebo.domain.Expense;
import com.aliciagar2.mykakebo.domain.KakeboCategory;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseDateOutsideMonthException;
import com.aliciagar2.mykakebo.domain.exceptions.ExpenseNotFoundException;
import com.aliciagar2.mykakebo.domain.exceptions.YearMonthInvalidException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
    private ExpenseService expenseService;

    private static Expense expense(Long id, KakeboCategory category, String amount, LocalDate date, String note) {
        return new Expense(id, null, YearMonth.from(date), category, new BigDecimal(amount), date, note);
    }

    @Test
    void createAcceptsNoteAtMaxLength() throws Exception {
        String maxNote = "n".repeat(500);
        when(expenseService.create(anyInt(), anyInt(), any(), any(), any(), any())).thenAnswer(invocation -> {
            KakeboCategory category = invocation.getArgument(2);
            BigDecimal amount = invocation.getArgument(3);
            LocalDate date = invocation.getArgument(4);
            String note = invocation.getArgument(5);
            return expense(12L, category, amount.toPlainString(), date, note);
        });

        String body = objectMapper.writeValueAsString(new TestExpenseRequest(KakeboCategory.CULTURE, new BigDecimal("5.00"), LocalDate.of(2026, 1, 1), maxNote));

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.note").value(maxNote));
    }

    @Test
    void createAcceptsSmallestPositiveAmount() throws Exception {
        when(expenseService.create(anyInt(), anyInt(), any(), any(), any(), any())).thenAnswer(invocation -> {
            KakeboCategory category = invocation.getArgument(2);
            BigDecimal amount = invocation.getArgument(3);
            LocalDate date = invocation.getArgument(4);
            String note = invocation.getArgument(5);
            return expense(11L, category, amount.toPlainString(), date, note);
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
    void createPersistsExpenseAndReturns201() throws Exception {
        when(expenseService.create(anyInt(), anyInt(), any(), any(), any(), any())).thenAnswer(invocation -> {
            KakeboCategory category = invocation.getArgument(2);
            BigDecimal amount = invocation.getArgument(3);
            LocalDate date = invocation.getArgument(4);
            String note = invocation.getArgument(5);
            return expense(10L, category, amount.toPlainString(), date, note);
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
    void createRejects400WhenDateIsOutsideUrlMonth() throws Exception {
        // The date-outside-month rule now lives in ExpenseService, which is mocked here, so this
        // test only proves the HTTP mapping; ExpenseServiceTest covers the rule itself.
        when(expenseService.create(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenThrow(new ExpenseDateOutsideMonthException("date is outside month"));

        String body = """
                {"category":"SURVIVAL","amount":10.00,"date":"2026-06-15","note":null}
                """;

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRejectsAmountWithTooManyDecimalPlaces() throws Exception {
        String body = """
                {"category":"SURVIVAL","amount":42.5001,"date":"2026-01-01","note":null}
                """;

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(expenseService);
    }

    @Test
    void createRejectsMalformedJsonBody() throws Exception {
        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not valid json"))
                .andExpect(status().isBadRequest());
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

        verifyNoInteractions(expenseService);
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
    void createRejectsNoteOverMaxLength() throws Exception {
        String tooLongNote = "n".repeat(501);
        String body = objectMapper.writeValueAsString(new TestExpenseRequest(KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2026, 1, 1), tooLongNote));

        mockMvc.perform(post("/api/months/2026/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
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

        verifyNoInteractions(expenseService);
    }

    @Test
    void deleteRemovesExistingExpense() throws Exception {
        mockMvc.perform(delete("/api/months/2026/1/expenses/5"))
                .andExpect(status().isNoContent());

        verify(expenseService).delete(5L);
    }

    @Test
    void deleteReturns404WhenExpenseDoesNotExist() throws Exception {
        doThrow(new ExpenseNotFoundException("Expense not found: 999")).when(expenseService).delete(999L);

        mockMvc.perform(delete("/api/months/2026/1/expenses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listFiltersByCategoryWhenProvided() throws Exception {
        when(expenseService.list(2026, 1, KakeboCategory.OPTIONAL))
                .thenReturn(List.of(expense(2L, KakeboCategory.OPTIONAL, "30.00", LocalDate.of(2026, 1, 20), "Dining")));

        mockMvc.perform(get("/api/months/2026/1/expenses").param("category", "OPTIONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("OPTIONAL"));

        verify(expenseService).list(2026, 1, KakeboCategory.OPTIONAL);
    }

    @Test
    void listIgnoresUnknownCategoryValueAsBadRequest() throws Exception {
        mockMvc.perform(get("/api/months/2026/1/expenses").param("category", "NOT_A_CATEGORY"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturns400ForInvalidMonth() throws Exception {
        when(expenseService.list(anyInt(), anyInt(), any()))
                .thenThrow(new YearMonthInvalidException("Invalid year/month: 2026/13"));

        mockMvc.perform(get("/api/months/2026/13/expenses"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturns400ForZeroMonth() throws Exception {
        when(expenseService.list(anyInt(), anyInt(), any()))
                .thenThrow(new YearMonthInvalidException("Invalid year/month: 2026/0"));

        mockMvc.perform(get("/api/months/2026/0/expenses"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturnsEmptyArrayWhenNoExpensesInMonth() throws Exception {
        when(expenseService.list(2026, 2, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/months/2026/2/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listReturnsExpensesInMonth() throws Exception {
        when(expenseService.list(2026, 1, null)).thenReturn(List.of(
                expense(1L, KakeboCategory.SURVIVAL, "50.00", LocalDate.of(2026, 1, 15), "Groceries")));

        mockMvc.perform(get("/api/months/2026/1/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].category").value("SURVIVAL"))
                .andExpect(jsonPath("$[0].note").value("Groceries"));
    }

    @Test
    void updateModifiesExistingExpense() throws Exception {
        when(expenseService.update(anyInt(), anyInt(), eq(5L), any(), any(), any(), any()))
                .thenAnswer(invocation -> {
                    KakeboCategory category = invocation.getArgument(3);
                    BigDecimal amount = invocation.getArgument(4);
                    LocalDate date = invocation.getArgument(5);
                    String note = invocation.getArgument(6);
                    return expense(5L, category, amount.toPlainString(), date, note);
                });

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
    void updateRejects400WhenDateIsOutsideUrlMonth() throws Exception {
        when(expenseService.update(anyInt(), anyInt(), any(), any(), any(), any(), any()))
                .thenThrow(new ExpenseDateOutsideMonthException("date is outside month"));

        String body = """
                {"category":"SURVIVAL","amount":10.00,"date":"2026-06-15","note":null}
                """;

        mockMvc.perform(put("/api/months/2026/1/expenses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReturns404WhenConcurrentDeleteWinsTheRace() throws Exception {
        // ExpenseService.update() translates a lost optimistic-lock race into ExpenseNotFoundException;
        // the race mechanics themselves are covered by ExpenseServiceTest, not here.
        when(expenseService.update(anyInt(), anyInt(), eq(5L), any(), any(), any(), any()))
                .thenThrow(new ExpenseNotFoundException("Expense not found: 5"));

        String body = """
                {"category":"OPTIONAL","amount":99.99,"date":"2026-01-16","note":"Updated"}
                """;

        mockMvc.perform(put("/api/months/2026/1/expenses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns404WhenExpenseDoesNotExist() throws Exception {
        when(expenseService.update(anyInt(), anyInt(), eq(999L), any(), any(), any(), any()))
                .thenThrow(new ExpenseNotFoundException("Expense not found: 999"));

        String body = """
                {"category":"SURVIVAL","amount":10.00,"date":"2026-01-01","note":null}
                """;

        mockMvc.perform(put("/api/months/2026/1/expenses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    private record TestExpenseRequest(KakeboCategory category, BigDecimal amount, LocalDate date, String note) {
    }
}
