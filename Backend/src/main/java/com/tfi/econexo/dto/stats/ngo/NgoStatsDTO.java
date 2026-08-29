package com.tfi.econexo.dto.stats.ngo;

import com.tfi.econexo.dto.stats.donor.CategoryStatsDTO;
import com.tfi.econexo.dto.stats.donor.RecentDonationDTO;
import com.tfi.econexo.dto.stats.driver.TopBusinessDTO;
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

        @Schema(description = "Total money donated")
        Double totalMoney,

        @Schema(description = "Current month money donated")
        Double currentMoney,

        @Schema(description = "Top categories")
        List<CategoryStatsDTO> topCategories,

        @Schema(description = "Recent donations")
        List<RecentDonationDTO> recentDonations,

        @Schema(description = "Donation counts grouped by status, in the selected range")
        List<Object[]> funnel,

        @Schema(description = "Top donor businesses in range")
        List<TopBusinessDTO> topBusinesses,

        @Schema(description = "Monthly trend of kilos and money in range")
        List<MonthlyNgoTrendDTO> monthlyTrend,

        @Schema(description = "Comparison vs previous equivalent period, present only when an explicit date range is selected")
        NgoStatsComparisonDTO comparison
) {
}
