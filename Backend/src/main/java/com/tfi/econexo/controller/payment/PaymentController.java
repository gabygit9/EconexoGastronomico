package com.tfi.econexo.controller.payment;

import com.tfi.econexo.dto.payment.MoneyDonationDTO;
import com.tfi.econexo.dto.payment.PaymentRequestDTO;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.service.payment.MoneyDonationService;
import com.tfi.econexo.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/my-donations")
    @Operation(summary = "Get my donations", description = "Get my donations")
    @PreAuthorize("hasRole('ROLE_NGO')")
    public ResponseEntity<Page<MoneyDonationDTO>> getMyDonations(
            Authentication authentication,
            @RequestParam(required = false) DonationStatus status,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        String ngoEmail = authentication.getName();
        return ResponseEntity.ok(moneyDonationService.getDonations(ngoEmail, status, pageable ));
    }
}
