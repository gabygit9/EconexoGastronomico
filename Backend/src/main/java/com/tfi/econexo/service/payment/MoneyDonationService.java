package com.tfi.econexo.service.payment;

import com.tfi.econexo.dto.payment.MoneyDonationDTO;
import com.tfi.econexo.dto.payment.PaymentRequestDTO;
import com.tfi.econexo.model.enums.DonationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MoneyDonationService {

    Long createMoneyDonation(PaymentRequestDTO dto, Optional<String> donorEmail);
    Page<MoneyDonationDTO> getDonations(String ngoEmail, DonationStatus status, Pageable pageable);

}
