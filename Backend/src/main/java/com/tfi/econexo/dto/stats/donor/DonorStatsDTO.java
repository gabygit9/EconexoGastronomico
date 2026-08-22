package com.tfi.econexo.dto.stats.donor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Donor statistics")
public record DonorStatsDTO(

        @Schema(description = "Total kilograms in the selected range (all-time if no filter)")
        Double totalKilos,

        @Schema(description = "Total money donated in the selected range (all-time if no filter)")
        Double totalMoney,

        @Schema(description = "Total donations created in the selected range (all-time if no filter)")
        Long totalDonations,

        @Schema(description = "List of top categories")
        List<CategoryStatsDTO> topCategories,

        @Schema(description = "Current month impact")
        Double currentMonthImpact,

        @Schema(description = "Previous month impact")
        Double prevMonthImpact,

        @Schema(description = "Current money")
        Double currentMoney,

        @Schema(description = "Previous month money")
        Double prevMoney,

        @Schema(description = "Recent donations")
        List<RecentDonationDTO> recentDonations,

        @Schema(description = "Completed (DELIVERED) donations in range")
        Long completedDonations,

        @Schema(description = "Success rate: completed / total donations in range, as percentage")
        Double successRate,

        @Schema(description = "Estimated rations (kilos x 2)")
        Double estimatedRations,

        @Schema(description = "Top NGOs benefited by this donor in range")
        List<TopNgoDTO> topNgos,

        @Schema(description = "Monthly trend of kilos and money in range")
        List<MonthlyDonorTrendDTO> monthlyTrend,

        @Schema(description = "Comparison vs previous equivalent period, present only when an explicit date range is selected")
        DonorStatsComparisonDTO comparison,

        @Schema(description = "Donation counts grouped by status, in the selected range")
        List<Object[]> funnel,

        @Schema(description = "Donation intensity by day/hour, in the selected range")
        List<Object[]> heatmap
) {
}
