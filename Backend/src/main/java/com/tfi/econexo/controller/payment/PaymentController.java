package com.tfi.econexo.controller.payment;

import com.tfi.econexo.dto.payment.PaymentRequestDTO;
import com.tfi.econexo.service.payment.MoneyDonationService;
import com.tfi.econexo.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Endpoints for payment management")
public class PaymentController {

    private final PaymentService paymentService;
    private final MoneyDonationService moneyDonationService;

    @PostMapping("/create-preference")
    @Operation(summary = "Create a payment preference", description = "Create a payment preference")
    public ResponseEntity<Map<String, String>> createPreference(@RequestBody PaymentRequestDTO dto){
        String initPoint = paymentService.createPreference(dto);
        return ResponseEntity.ok(Map.of("initPoint", initPoint));
    }

    @PostMapping("/money-donations")
    @Operation(summary = "Create a money donation", description = "Create a money donation")
    public ResponseEntity<Long> initiateDonation(@RequestBody PaymentRequestDTO dto, Authentication authentication) {
        Optional<String> email = (authentication != null && !"anonymousUser".equals(authentication.getName()))
                ? Optional.of(authentication.getName())
                : Optional.empty();
        Long donationId = moneyDonationService.createMoneyDonation(dto, email);
        return ResponseEntity.ok(donationId);
    }
}
