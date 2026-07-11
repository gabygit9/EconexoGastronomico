package com.tfi.econexo.service.impl.payment;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.tfi.econexo.dto.payment.PaymentRequestDTO;
import com.tfi.econexo.service.payment.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;


@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public String createPreference(PaymentRequestDTO dto) {
        try {
            PreferenceClient client = new PreferenceClient();

            if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto es inválido");
            }

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(dto.description())
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(dto.amount())
                    .build();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(Collections.singletonList(itemRequest))
                    .externalReference(String.valueOf(dto.donationId()))
                    .backUrls(PreferenceBackUrlsRequest.builder()
                            .success("http://localhost:4200/donations/success")
                            .pending("http://localhost:4200/donations/pending")
                            .failure("http://localhost:4200/donations/failure")
                            .build())
                    // TODO: Habilitar autoReturn una vez configurado entorno de producción.
                    //.autoReturn("approved")
                    .metadata(Map.of("ngo_id", String.valueOf(dto.ngoId())))
                    .build();

            Preference preference = client.create(request);

            return preference.getInitPoint();

        } catch (MPApiException e) {
            System.err.println("Error detalle de MP: " + e.getApiResponse().getContent());
            throw new RuntimeException("Error en Mercado Pago: " + e.getMessage());
        } catch (MPException e) {
            throw new RuntimeException(e);
        }
    }
}
