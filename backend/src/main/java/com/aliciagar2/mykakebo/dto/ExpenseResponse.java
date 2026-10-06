package com.aliciagar2.mykakebo.dto;

import com.aliciagar2.mykakebo.domain.KakeboCategory;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        KakeboCategory category,
        BigDecimal amount,
        LocalDate date,
        String note
) {}