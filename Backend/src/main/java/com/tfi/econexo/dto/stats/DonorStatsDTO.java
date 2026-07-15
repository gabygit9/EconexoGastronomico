package com.tfi.econexo.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Donor statistics")
public record DonorStatsDTO(

        @Schema(description = "Total kilograms of donations")
        Double totalKilosDonated,

        @Schema(description = "Total money donated")
        Double totalMoneyDonated,

        @Schema(description = "Number of charities helped")
        Long charitiesHelped,

        @Schema(description = "List of top categories")
        List<CategoryStatsDTO> topCategories,

        @Schema(description = "Estimated meals")
        Double estimatedMeals
) {
}
