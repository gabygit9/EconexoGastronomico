package com.tfi.econexo.service.payment;

import com.tfi.econexo.dto.payment.PaymentRequestDTO;

import java.util.Optional;

public interface MoneyDonationService {

    Long createMoneyDonation(PaymentRequestDTO dto, Optional<String> donorEmail);
}
