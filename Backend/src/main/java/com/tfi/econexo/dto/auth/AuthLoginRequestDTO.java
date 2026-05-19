package com.tfi.econexo.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDTO(
    @NotBlank String email,
    @NotBlank String password
){}
