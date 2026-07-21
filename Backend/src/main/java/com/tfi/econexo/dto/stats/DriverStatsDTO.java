package com.tfi.econexo.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Driver statistics")
public record DriverStatsDTO(

        @Schema(description = "Total deliveries")
        Long totalDeliveries,

        @Schema(description = "Total kilograms transported")
        Double totalKilosTransported,

        @Schema(description = "Average distance in km")
        Double averageDistanceKm,

        @Schema(description = "Punctuality percentage")
        Double punctualityPercentage,

        @Schema(description = "Activity by hour")
        List<Integer> activityByHour,

        @Schema(description = "Average kilograms per delivery")
        Double avgKilosPerDelivery,

        @Schema(description = "Active days")
        Long activeDays,

        @Schema(description = "Monthly punctuality")
        List<Map<String, Object>> monthlyPunctuality
) {
}
