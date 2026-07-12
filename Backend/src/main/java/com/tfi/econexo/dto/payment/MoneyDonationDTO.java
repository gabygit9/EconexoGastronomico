package com.tfi.econexo.dto.payment;

import com.tfi.econexo.model.enums.DonationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Money donation details")
public record MoneyDonationDTO(

        @Schema(description = "Id of the donation")
        Long id,

        @Schema(description = "Amount of the donation")
        BigDecimal amount,

        @Schema(description = "Status of the donation")
        DonationStatus status,

        @Schema(description = "Id of the NGO")
        Long ngoId,

        @Schema(description = "Id of the donor")
        Long donorId,

        @Schema(description = "Date of the donation")
        LocalDateTime createdDate
) {
}
