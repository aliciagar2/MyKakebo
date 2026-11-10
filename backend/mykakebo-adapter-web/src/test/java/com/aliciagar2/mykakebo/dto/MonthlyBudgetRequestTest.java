package com.aliciagar2.mykakebo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonthlyBudgetRequestTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsNegativeFixedExpenses() {
        MonthlyBudgetRequest request =
                new MonthlyBudgetRequest(new BigDecimal("1000.00"), new BigDecimal("-1.00"), new BigDecimal("0.00"));

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNegativeIncome() {
        MonthlyBudgetRequest request =
                new MonthlyBudgetRequest(new BigDecimal("-1.00"), new BigDecimal("0.00"), new BigDecimal("0.00"));

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNegativeSavingsGoal() {
        MonthlyBudgetRequest request =
                new MonthlyBudgetRequest(new BigDecimal("1000.00"), new BigDecimal("0.00"), new BigDecimal("-1.00"));

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNullFixedExpenses() {
        MonthlyBudgetRequest request = new MonthlyBudgetRequest(new BigDecimal("1000.00"), null, new BigDecimal("0.00"));

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNullIncome() {
        MonthlyBudgetRequest request = new MonthlyBudgetRequest(null, new BigDecimal("0.00"), new BigDecimal("0.00"));

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNullSavingsGoal() {
        MonthlyBudgetRequest request = new MonthlyBudgetRequest(new BigDecimal("1000.00"), new BigDecimal("0.00"), null);

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsTooManyFractionDigits() {
        MonthlyBudgetRequest request = new MonthlyBudgetRequest(
                new BigDecimal("1000.123"), new BigDecimal("0.00"), new BigDecimal("0.00"));

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void validatesSuccessfullyWithAllConstraintsSatisfied() {
        MonthlyBudgetRequest request = new MonthlyBudgetRequest(
                new BigDecimal("1000.00"), new BigDecimal("500.00"), new BigDecimal("100.00"));

        Set<ConstraintViolation<MonthlyBudgetRequest>> violations = VALIDATOR.validate(request);

        assertThat(violations).isEmpty();
    }
}
