package com.tfi.econexo.service.payment;

import com.tfi.econexo.dto.payment.PaymentRequestDTO;

public interface PaymentService {

    String createPreference(PaymentRequestDTO dto);
}
