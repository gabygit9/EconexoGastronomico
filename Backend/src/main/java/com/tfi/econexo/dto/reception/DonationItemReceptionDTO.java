package com.tfi.econexo.dto.reception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Donation item details")
public record DonationItemReceptionDTO(

        @Schema(description = "Item ID")
        Long itemId,

        @Schema(description = "Product name")
        String productName,

        @Schema(description = "Expected quantity")
        Double expectedQuantity,

        @Schema(description = "Unit of measure")
        String unitOfMeasure,

        @Schema(description = "Description")
        String description
) {
}
