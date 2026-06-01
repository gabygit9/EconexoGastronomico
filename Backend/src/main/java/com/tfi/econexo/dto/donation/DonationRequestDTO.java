package com.tfi.econexo.dto.donation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record DonationRequestDTO(
        @NotNull LocalDateTime pickupStartTime,
        @NotNull LocalDateTime pickupEndTime,
        @NotEmpty List<DonationItemRequestDTO> items
        ) {
}
