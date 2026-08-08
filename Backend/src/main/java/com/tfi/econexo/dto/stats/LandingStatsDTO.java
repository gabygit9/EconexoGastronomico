package com.tfi.econexo.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Public aggregate stats for the landing page")
public record LandingStatsDTO(

        @Schema(description = "Total kilograms of food delivered")
        BigDecimal totalKilosDelivered,

        @Schema(description = "Total number of completed deliveries")
        Long totalDeliveries,

        @Schema(description = "Total amount of money donated")
        BigDecimal totalMoneyDonated,

        @Schema(description = "Number of approved NGOs in the network")
        Long totalNgos,

        @Schema(description = "Number of approved donor businesses in the network")
        Long totalDonors,

        @Schema(description = "Number of approved volunteer drivers in the network")
        Long totalDrivers
) {
}
