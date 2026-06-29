package com.tfi.econexo.dto.logistics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO to create a reception evidence")
public record NgoReceptionEvidenceDTO(

        @Schema(description = "List of donation items received")
        List<Long> receivedItems,

        @Schema(description = "URL of the manager's NGO signature")
        String ngoSignatureUrl,

        @Schema(description = "Indicates if the manager has accepted the disclaimer")
        boolean disclaimerAccepted
) {
}
