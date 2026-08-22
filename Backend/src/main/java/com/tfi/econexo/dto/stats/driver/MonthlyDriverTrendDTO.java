package com.tfi.econexo.dto.stats.driver;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Monthly trend point for a driver")
public record MonthlyDriverTrendDTO(
        int year,
        int month,
        Long deliveries,
        Double kilos
) {
}
