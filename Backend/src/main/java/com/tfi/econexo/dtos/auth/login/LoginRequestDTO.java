package com.tfi.econexo.dtos.auth.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(

        @NotNull
        @Email
        String email,

        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {
}
