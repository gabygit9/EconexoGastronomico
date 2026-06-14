package com.tfi.econexo.dto.donation.catalog;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product of catalog")
public record ProductDTO(

        @Schema(description = "ID of the product")
        Long id,

        @Schema(description = "Name of the product")
        String name,

        @Schema(description = "ID of the category")
        Long categoryId,

        @Schema(description = "The food requires refrigeration")
        boolean requiresRefrigeration,

        @Schema(description = "The product is in its original packaging")
        boolean isOriginalPackaging
) {
}
