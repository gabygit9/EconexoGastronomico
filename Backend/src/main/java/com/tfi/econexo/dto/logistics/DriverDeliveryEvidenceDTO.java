package com.tfi.econexo.dto.logistics;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO to create a delivery evidence")
public record DriverDeliveryEvidenceDTO(
        @Schema(description = "Temperature of the delivery", example = "4.5")
        Double temperature,

        @Schema(description = "URL of the evidence photo")
        String evidencePhotoUrl,

        @Schema(description = "URL of the driver's signature")
        String driverSignatureUrl

) {
}
