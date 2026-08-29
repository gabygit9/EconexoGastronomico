package com.tfi.econexo.dto.stats.ngo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Monthly trend point for an NGO")
public record MonthlyNgoTrendDTO(
        int year,
        int month,
        Double kilos,
        Double money
) {
}
