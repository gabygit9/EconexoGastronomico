package com.tfi.econexo.dtos.auth.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDTO(

        @JsonProperty("user_id")
        Long userId,
        String email,
        String role
        //String token
) {
}
