package com.tfi.econexo.controllers.auth;

import com.tfi.econexo.dtos.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dtos.auth.donor.DonorResponseDTO;
import com.tfi.econexo.dtos.auth.driver.DriverRegistrationDTO;
import com.tfi.econexo.dtos.auth.driver.DriverResponseDTO;
import com.tfi.econexo.dtos.auth.login.LoginRequestDTO;
import com.tfi.econexo.dtos.auth.login.LoginResponseDTO;
import com.tfi.econexo.dtos.auth.organization.OrganizationRegistrationDTO;
import com.tfi.econexo.dtos.auth.organization.OrganizationResponseDTO;
import com.tfi.econexo.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/donor")
    public ResponseEntity<DonorResponseDTO> registerDonor(@RequestBody @Valid DonorRegistrationDTO donorRegistrationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerDonor(donorRegistrationDTO));
    }

    @PostMapping("/register/driver")
    public ResponseEntity<DriverResponseDTO> registerDriver(@RequestBody @Valid DriverRegistrationDTO driverRegistrationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerDriver(driverRegistrationDTO));
    }

    @PostMapping("/register/organization")
    public ResponseEntity<OrganizationResponseDTO> registerOrganization(@RequestBody @Valid OrganizationRegistrationDTO organizationRegistrationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerOrganization(organizationRegistrationDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }
}
