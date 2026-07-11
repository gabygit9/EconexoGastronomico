package com.tfi.econexo.service.impl.payment;

import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.payment.Payment;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.repository.donation.MoneyDonationRepository;
import com.tfi.econexo.service.payment.WebhookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final MoneyDonationRepository moneyDonationRepository;
    private static final Logger logger = LoggerFactory.getLogger(WebhookServiceImpl.class);

    @Override
    @Transactional
    public void processWebhook(String merchantOrderId) {

        try {
            //usar sdk de MP para consultar los detalles de la orden
            MerchantOrderClient client = new MerchantOrderClient();
            MerchantOrder order = client.get(Long.parseLong(merchantOrderId));

            logger.info("Order status: {}, externalReference: {}",
                    order.getOrderStatus(), order.getExternalReference());

            String donationIdStr = null;

            if (order.getExternalReference() != null) {
                donationIdStr = order.getExternalReference();
            } else if (order.getPayments() != null && !order.getPayments().isEmpty()) {
                Long paymentId = order.getPayments().get(0).getId();
                PaymentClient paymentClient = new PaymentClient();
                Payment payment = paymentClient.get(paymentId);
                donationIdStr = payment.getExternalReference();
            }

            if (donationIdStr != null) {
                Long donationId = Long.parseLong(donationIdStr);

                moneyDonationRepository.findById(donationId).ifPresent(donation -> {
                    if ("paid".equals(order.getOrderStatus())) {
                        donation.setStatus(DonationStatus.COMPLETED);
                        moneyDonationRepository.save(donation);
                        logger.info("Donación {} marcada como COMPLETADA.", donationId);
                    }
                });
            } else {
                logger.error("No se pudo extraer externalReference de la orden: {}", merchantOrderId);
            }
        } catch (Exception e) {
            logger.error("Critical error processing MP Webhook: {}", e.getMessage());
            throw new RuntimeException("Error in Webhook", e);
        }
    }
}
