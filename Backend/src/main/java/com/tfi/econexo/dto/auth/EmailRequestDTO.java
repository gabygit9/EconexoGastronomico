package com.tfi.econexo.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Email request")
public record EmailRequestDTO(

        @Schema(description = "Email address", example = "email@example.com")
        @NotNull String email
) {
}
