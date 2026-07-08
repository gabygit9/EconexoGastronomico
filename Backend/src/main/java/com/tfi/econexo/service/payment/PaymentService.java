package com.tfi.econexo.service.payment;

import java.math.BigDecimal;

public interface PaymentService {

    String createPreference(BigDecimal amount, String description);
}
