package com.tfi.econexo.dto.stats.driver;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Comparison vs. previous equivalent period (only present when a date range is selected)")
public record DriverStatsComparisonDTO(
        Double totalDeliveriesChangePercent,
        Long totalDeliveriesPrev,
        Double totalKilosChangePercent,
        Double totalKilosPrev,
        Double punctualityDeltaPoints,
        Double punctualityPrev
) {
}
