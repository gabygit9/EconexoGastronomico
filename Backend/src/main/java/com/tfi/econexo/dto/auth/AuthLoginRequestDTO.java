package com.tfi.econexo.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request data")
public record AuthLoginRequestDTO(
        @NotBlank
        @Schema(description = "User email", example = "user@example.com")
        String email,

        @NotBlank
        @Schema(description = "User password", example = "password123")
        String password
){}
