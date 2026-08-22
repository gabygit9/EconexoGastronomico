package com.tfi.econexo.dto.stats.driver;

import com.tfi.econexo.dto.stats.donor.TopNgoDTO;
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
        List<Map<String, Object>> monthlyPunctuality,

        @Schema(description = "Trip counts grouped by status, in the selected range")
        List<Object[]> funnel,

        @Schema(description = "Top businesses (pickup points) in range")
        List<TopBusinessDTO> topBusinesses,

        @Schema(description = "Top NGOs (delivery points) in range")
        List<TopNgoDTO> topNgos,

        @Schema(description = "Monthly trend of deliveries and kilos in range")
        List<MonthlyDriverTrendDTO> monthlyTrend,

        @Schema(description = "Comparison vs previous equivalent period, present only when an explicit date range is selected")
        DriverStatsComparisonDTO comparison
) {
}
