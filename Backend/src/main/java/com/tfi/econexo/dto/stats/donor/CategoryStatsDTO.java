package com.tfi.econexo.dto.stats.donor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category statistics")
public record CategoryStatsDTO(

        @Schema(description = "Name of the category")
        String categoryName,

        @Schema(description = "Quantity of the category")
        Double quantity
) {
}
