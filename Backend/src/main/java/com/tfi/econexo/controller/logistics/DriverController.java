package com.tfi.econexo.controller.logistics;

import com.tfi.econexo.dto.auth.logistics.DriverResponseDTO;
import com.tfi.econexo.service.logistics.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Endpoints for drivers")
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/profile")
    @Operation(summary = "Get my profile", description = "Return the profile of the authenticated Driver.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<DriverResponseDTO> getMyProfile(Authentication authentication){
        String email = authentication.getName();
        DriverResponseDTO profile = this.driverService.getProfileByEmail(email);
        return ResponseEntity.ok(profile);
    }
}
