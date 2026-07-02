package com.tfi.econexo.dto.reception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Received item details")
public record ReceivedItemDTO(

        @Schema(description = "Item ID")
        Long itemId,

        @Schema(description = "Received quantity")
        Double receivedQuantity
) {
}
