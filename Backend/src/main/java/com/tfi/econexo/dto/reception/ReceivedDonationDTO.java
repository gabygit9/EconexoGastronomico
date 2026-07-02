package com.tfi.econexo.dto.reception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Received donation details")
public record ReceivedDonationDTO(

        @Schema(description = "Comments")
        String comments
) {
}
