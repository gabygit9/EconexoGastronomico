package com.tfi.econexo.utils.notification;

public interface EmailService {

    void sendApprovalEmail(String toEmail, String recipientEmail, String role);

    void sendPasswordResetEmail(String toEmail, String resetLink);

    void sendGenericNotification(String toEmail, String subject, String message);
}
