package com.tfi.econexo.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Stats for an NGO")
public record NgoStatsDTO(

        @Schema(description = "Total kilograms of donations received")
        Double totalKilos,

        @Schema(description = "Unique donors")
        Long uniqueDonors,

        @Schema(description = "Ratio of donations to total kilograms")
        Double efficiencyRatio,

        @Schema(description = "Monthly impact")
        Double monthlyImpact,

        @Schema(description = "Previous month impact")
        Double prevMonthImpact,

        @Schema(description = "Top categories")
        List<CategoryStatsDTO> topCategories,

        @Schema(description = "Recent donations")
        List<RecentDonationDTO> recentDonations
) {
}
