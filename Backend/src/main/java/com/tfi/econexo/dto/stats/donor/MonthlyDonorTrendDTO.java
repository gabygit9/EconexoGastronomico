package com.tfi.econexo.dto.stats.donor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Monthly trend point for a donor")
public record MonthlyDonorTrendDTO(
        int year,
        int month,
        Double kilos,
        Double money
) {
}
