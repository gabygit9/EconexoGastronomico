package com.tfi.econexo.dto.stats;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin stats")
public record AdminStatsDTO(

        @Schema(description = "Total donations")
        Long totalDonations,

        @Schema(description = "Total users")
        Long totalUsers,

        @Schema(description = "Total platform revenue")
        Double totalPlatformRevenue
) {
}
