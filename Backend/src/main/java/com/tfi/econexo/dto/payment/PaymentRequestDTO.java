package com.tfi.econexo.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Payment request")
public record PaymentRequestDTO(

        @Schema(description = "Donation id")
        Long donationId,

        @Schema(description = "Amount of the donation")
        BigDecimal amount,

        @Schema(description = "Description of the donation")
        String description
) {
}
