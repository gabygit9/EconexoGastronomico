package com.tfi.econexo.service.impl.payment;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.tfi.econexo.service.payment.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;


@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public String createPreference(BigDecimal amount, String description) {
        try {
            PreferenceClient client = new PreferenceClient();

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(description)
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(amount)
                    .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:4200/donations/success")
                    .failure("http://localhost:4200/donations/failure")
                    .pending("http://localhost:4200/donations/pending")
                    .build();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(Collections.singletonList(itemRequest))
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .build();

            Preference preference = client.create(request);

            return preference.getInitPoint();

        } catch (Exception e) {
            logger.error("Error creating Mercado Pago preferences: " + e.getMessage(), e);
            throw new RuntimeException("Error creating Mercado Pago preferences: " + e.getMessage(), e);
        }


    }
}
