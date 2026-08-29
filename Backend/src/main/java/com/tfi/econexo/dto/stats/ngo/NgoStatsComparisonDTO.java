package com.tfi.econexo.dto.stats.ngo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Comparison vs. previous equivalent period (only present when a date range is selected)")
public record NgoStatsComparisonDTO(
        Double totalKilosChangePercent,
        Double totalKilosPrev,
        Double totalMoneyChangePercent,
        Double totalMoneyPrev,
        Double efficiencyDeltaPoints,
        Double efficiencyPrev
) {
}
