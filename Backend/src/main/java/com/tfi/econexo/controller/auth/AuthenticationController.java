package com.tfi.econexo.controller.auth;

import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.dto.auth.login.AuthLoginRequestDTO;
import com.tfi.econexo.dto.auth.login.AuthResponseDTO;
import com.tfi.econexo.dto.auth.logistics.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.logistics.DriverResponseDTO;
import com.tfi.econexo.dto.auth.ngo.NgoRegistrationDTO;
import com.tfi.econexo.dto.auth.ngo.NgoResponseDTO;
import com.tfi.econexo.service.auth.AuthService;
import com.tfi.econexo.service.auth.BlacklistedTokenService;
import com.tfi.econexo.service.impl.auth.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    private final AuthService authService;
    private final BlacklistedTokenService blacklistedTokenService;

    @PostMapping("/login")
    @Operation(summary = "User login",
            description = "Authenticate user credentials (email and password). If correct, generates and returns a JWT token along with the corresponding roles.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
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
    public ResponseEntity<AuthResponseDTO> login (@RequestBody @Valid AuthLoginRequestDTO userRequest) {
        return new ResponseEntity<>(this.userDetailsService.loginUser(userRequest), HttpStatus.CREATED);
    }

    @PostMapping("/register/donor")
    @Operation(summary = "Register a new donor",
            description = "Post a new business in the platform. Create its access credentials with DONOR rol and link its business profile with geolocalization data.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Donor successfully created.",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = DonorResponseDTO.class)) }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid credentials. Or email/taxId already exists. Returns an error message.",
                    content = @Content
            )
    })
    public ResponseEntity<DonorResponseDTO> registerDonor(@RequestBody @Valid DonorRegistrationDTO donorDTO) {
        return new ResponseEntity<>(this.authService.registerDonor(donorDTO), HttpStatus.CREATED);
    }

    @PostMapping("/register/ngo")
    @Operation(summary = "Register a new ngo",
            description = "Post a new ngo in the platform. Create its access credentials with NGO rol and link its ngo profile with geolocalization data.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ngo successfully created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = NgoResponseDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid credentials. Or email/taxId already exists. Returns an error message.",
                    content = @Content
            )
    })
    public ResponseEntity<NgoResponseDTO> registerNgo(@RequestBody @Valid NgoRegistrationDTO ngoDTO) {
        return new ResponseEntity<>(this.authService.registerNgo(ngoDTO), HttpStatus.CREATED);
    }

    @PostMapping("/register/driver")
    @Operation(summary = "Register a new Driver",
               description = "Post a new driver in the platform. Create its access credentials with DRIVER rol and link its driver profile with geolocalization data.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Driver successfully created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DriverResponseDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid credentials. Or email/taxId already exists. Returns an error message.",
                    content = @Content
            )
    })
    public ResponseEntity<DriverResponseDTO> registerDriver(@RequestBody @Valid DriverRegistrationDTO driverDTO) {
        return new ResponseEntity<>(this.authService.registerDriver(driverDTO), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user",
            description = "Invalidates the user's JWT token by adding it to the blacklist.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            )
    })
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistedTokenService.blacklistToken(token);
            return ResponseEntity.ok("Logout successful");
        }
        return ResponseEntity.badRequest().body("No token provided");
    }
}
