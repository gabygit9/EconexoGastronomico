package com.tfi.econexo.dto.stats.donor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = " Comparison vs. previous equivalent period (only present when a date range is selected")
public record DonorStatsComparisonDTO(
        Double totalKilosChangePercent,
        Double totalKilosPrev,
        Double totalMoneyChangePercent,
        Double totalMoneyPrev,
        Double totalDonationsChangePercent,
        Long totalDonationsPrev,
        Double completedDonationsChangePercent,
        Long completedDonationsPrev
) {
}
