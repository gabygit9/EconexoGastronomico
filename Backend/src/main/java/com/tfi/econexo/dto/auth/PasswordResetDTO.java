package com.tfi.econexo.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Password reset request")
public record PasswordResetDTO(

        @Schema(description = "Password reset token", example = "1234567890")
        @NotNull String token,

        @Schema(description = "New password", example = "password123")
        @NotNull String newPassword
) {
}
