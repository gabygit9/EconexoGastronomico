package com.tfi.econexo.dto.donation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Rejection request for a donation")
public record RejectionRequestDTO(

        @Schema(description = "Base64 encoded photo")
        String photoBase64,

        @Schema(description = "Reason for rejection")
        String reason,

        @Schema(description = "Date of rejection")
        LocalDateTime date
) {
}
