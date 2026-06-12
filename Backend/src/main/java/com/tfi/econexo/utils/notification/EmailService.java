package com.tfi.econexo.utils.notification;

public interface EmailService {

    void sendApprovalEmail(String toEmail, String recipientEmail, String role);
}
