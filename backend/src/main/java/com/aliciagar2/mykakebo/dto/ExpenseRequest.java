package com.aliciagar2.mykakebo.dto;

import com.aliciagar2.mykakebo.domain.KakeboCategory;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotNull KakeboCategory category,
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate date,
        @Size(max = 500) String note
) {}