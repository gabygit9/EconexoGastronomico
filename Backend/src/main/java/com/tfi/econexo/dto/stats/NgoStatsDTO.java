package com.tfi.econexo.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Stats for an NGO")
public record NgoStatsDTO(

        @Schema(description = "Total kilograms of donations received")
        Double totalKilos,

        @Schema(description = "Unique donors")
        Long uniqueDonors,

        @Schema(description = "Ratio of donations to total kilograms")
        Double efficiencyRatio,

        @Schema(description = "Monthly impact comparison")
        Double monthlyImpactComparison
) {
}
