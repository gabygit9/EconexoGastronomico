package com.tfi.econexo.dto.donation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Notification response")
public record NotificationResponseDTO(
        @Schema(description = "Notification ID")
        Long id,

        @Schema(description = "Notification message")
        String message,

        @Schema(description = "True if the notification has been read, false otherwise")
        boolean isRead,

        @Schema(description = "Notification creation date")
        LocalDateTime createdAt
) {
}
