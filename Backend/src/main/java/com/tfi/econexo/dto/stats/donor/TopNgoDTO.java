package com.tfi.econexo.dto.stats.donor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Top NGO benefited by a donor, by kilos received")
public record TopNgoDTO(
        @Schema(description = "NGO name") String ngoName,
        @Schema(description = "Kilograms received") Double kilos
) {
}
