package com.tfi.econexo.dto.stats.driver;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Top business (pickup point) for a driver, by kilos")
public record TopBusinessDTO(

        @Schema(description = "Name of the business")
        String businessName,

        @Schema(description = "Total kilos collected")
        Double kilos
) {
}
