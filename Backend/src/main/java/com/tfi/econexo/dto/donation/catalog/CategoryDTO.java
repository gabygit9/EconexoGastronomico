package com.tfi.econexo.dto.donation.catalog;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category of product")
public record CategoryDTO(

        @Schema(description = "ID of the category")
        Long id,

        @Schema(description = "Description of the category")
        String description
) {
}
