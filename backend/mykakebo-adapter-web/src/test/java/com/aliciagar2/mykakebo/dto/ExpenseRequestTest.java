package com.aliciagar2.mykakebo.dto;

import com.aliciagar2.mykakebo.domain.KakeboCategory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseRequestTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsAmountWithTooManyFractionDigits() {
        ExpenseRequest request = new ExpenseRequest(
                KakeboCategory.SURVIVAL, new BigDecimal("10.123"), LocalDate.of(2024, 1, 10), "note");

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNonPositiveAmount() {
        ExpenseRequest request = new ExpenseRequest(
                KakeboCategory.SURVIVAL, new BigDecimal("0.00"), LocalDate.of(2024, 1, 10), "note");

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNoteLongerThan500Characters() {
        ExpenseRequest request = new ExpenseRequest(
                KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "a".repeat(501));

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNullCategory() {
        ExpenseRequest request = new ExpenseRequest(null, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void rejectsNullDate() {
        ExpenseRequest request = new ExpenseRequest(KakeboCategory.SURVIVAL, new BigDecimal("10.00"), null, "note");

        assertThat(VALIDATOR.validate(request)).isNotEmpty();
    }

    @Test
    void validatesSuccessfullyWithAllConstraintsSatisfied() {
        ExpenseRequest request = new ExpenseRequest(
                KakeboCategory.SURVIVAL, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), "note");

        Set<ConstraintViolation<ExpenseRequest>> violations = VALIDATOR.validate(request);

        assertThat(violations).isEmpty();
    }
}
