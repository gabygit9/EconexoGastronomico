package com.tfi.econexo.controllers.auth;

import com.tfi.econexo.dtos.auth.DonorRegistrationDTO;
import com.tfi.econexo.dtos.auth.DonorResponseDTO;
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
}
