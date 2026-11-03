package com.aliciagar2.mykakebo.domain;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YearMonthConverterTest {

    private final YearMonthConverter converter = new YearMonthConverter();

    @Test
    void convertToDatabaseColumnFormatsYearMonthAsIsoString() {
        assertThat(converter.convertToDatabaseColumn(YearMonth.of(2026, 9))).isEqualTo("2026-09");
    }

    @Test
    void convertToDatabaseColumnReturnsNullWhenAttributeIsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttributeParsesIsoString() {
        assertThat(converter.convertToEntityAttribute("2026-09")).isEqualTo(YearMonth.of(2026, 9));
    }

    @Test
    void convertToEntityAttributeReturnsNullWhenDbDataIsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
