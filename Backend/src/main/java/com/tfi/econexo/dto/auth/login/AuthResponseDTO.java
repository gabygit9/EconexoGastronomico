package com.tfi.econexo.dto.auth.login;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({"email", "message", "jwt", "status"})
@Schema(description = "Login response data")
public record AuthResponseDTO(
        @Schema(description = "User email authenticated", example = "user@example.com")
        String email,

        @Schema(description = "Descriptive message of the result of the operation", example = "Login successful")
        String message,

        @Schema(description = "JWT token that must be senden in the header", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String jwt,

        @Schema(description = "Boolean indicator of the result of the operation", example = "true")
        boolean status
) {
}
