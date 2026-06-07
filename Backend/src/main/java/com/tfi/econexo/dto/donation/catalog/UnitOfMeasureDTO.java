package com.tfi.econexo.dto.donation.catalog;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Unit of measure of product")
public record UnitOfMeasureDTO(

        @Schema(description = "ID of the unit of measure")
        Long id,

        @Schema(description = "Description of the unit of measure")
        String description
) {
}
