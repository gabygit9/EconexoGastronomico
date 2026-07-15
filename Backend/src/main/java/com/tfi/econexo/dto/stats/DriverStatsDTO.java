package com.tfi.econexo.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Driver statistics")
public record DriverStatsDTO(

        @Schema(description = "Total deliveries")
        Long totalDeliveries,

        @Schema(description = "Total kilograms transported")
        Double totalKilosTransported,

        @Schema(description = "Average distance in km")
        Double averageDistanceKm,

        @Schema(description = "Punctuality percentage")
        Double punctualityPercentage
) {
}
