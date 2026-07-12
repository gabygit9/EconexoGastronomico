package com.tfi.econexo.dto.reception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Received donation details")
public record ReceivedDonationDTO(

        @Schema(description = "Comments")
        String comments,

        @Schema(description = "List of items received")
        List<ReceivedItemDTO> receivedItems,

        @Schema(description = "Accepted law disclaimer")
        boolean acceptedDisclaimer,

        @Schema(description = "Signature URL")
        String signatureUrl
) {
}
