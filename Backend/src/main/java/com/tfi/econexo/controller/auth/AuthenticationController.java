package com.tfi.econexo.controller.auth;

import com.tfi.econexo.dto.auth.AuthLoginRequestDTO;
import com.tfi.econexo.dto.auth.AuthResponseDTO;
import com.tfi.econexo.service.impl.auth.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints to login and handle security tokens")
public class AuthenticationController {

    private final UserDetailsServiceImpl userDetailsService;

    @Operation(summary = "User login",
            description = "Authenticate user credentials (email and password). If correct, generates and returns a JWT token along with the corresponding roles.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authentication. Returns a valid JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content =  @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials. Returns an error message",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login (@RequestBody @Valid AuthLoginRequestDTO userRequest) {
        return new ResponseEntity<>(this.userDetailsService.loginUser(userRequest), HttpStatus.OK);

    }

}
