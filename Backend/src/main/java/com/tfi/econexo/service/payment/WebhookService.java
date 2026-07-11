package com.tfi.econexo.service.payment;

public interface WebhookService {

    void processWebhook(String merchantOrderId);
}
