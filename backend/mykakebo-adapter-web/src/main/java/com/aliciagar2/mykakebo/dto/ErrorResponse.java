package com.aliciagar2.mykakebo.dto;

import java.util.List;

public record ErrorResponse(String code, String message, List<FieldViolation> violations) {
}
