package com.tfi.econexo.controller.payment;

import com.tfi.econexo.dto.payment.PaymentRequestDTO;
import com.tfi.econexo.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Endpoints for payment management")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-preference")
    @Operation(summary = "Create a payment preference", description = "Create a payment preference")
    public ResponseEntity<Map<String, String>> createPreference(@RequestBody PaymentRequestDTO dto){
        String initPoint = paymentService.createPreference(dto);
        return ResponseEntity.ok(Map.of("initPoint", initPoint));
    }
}
