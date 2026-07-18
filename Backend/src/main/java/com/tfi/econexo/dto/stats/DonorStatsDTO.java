package com.tfi.econexo.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Donor statistics")
public record DonorStatsDTO(

        @Schema(description = "Total kilograms of donations")
        Double totalKilos,

        @Schema(description = "Total money donated")
        Double totalMoney,

        @Schema(description = "Total donations")
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
        List<RecentDonationDTO> recentDonations
) {
}
