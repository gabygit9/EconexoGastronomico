package com.tfi.econexo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Neighborhood lookup data")
public record NeighborhoodLookupDTO(
        @Schema( description = "Neighborhood Id", example = "1L")
        Long id,
        @Schema( description = "Neighborhood Name", example = "Centro")
        String name
) {
}
