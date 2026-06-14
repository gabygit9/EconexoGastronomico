package com.tfi.econexo.utils.notification;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements  EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendApprovalEmail(String toEmail, String recipientName, String role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            //Alias del remitente
            helper.setFrom(fromEmail, "Equipo EcoNexo");

            helper.setTo(toEmail);
            helper.setSubject("¡Tu cuenta ha sido aprobada!");

            //Generar html
            String htmlContent = buildHtmlContent(recipientName, role);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email enviado exitosamente a " + toEmail);
        } catch (Exception e) {
            System.err.println("Fallo al enviar el correo a " + toEmail + ": " + e.getMessage());
        }
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Equipo EcoNexo");
            helper.setTo(toEmail);
            helper.setSubject("Recuperación de Contraseña - EcoNexo");

            String htmlContent = buildPasswordResetHtml(resetLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        }catch (Exception e) {
            System.err.println("Fallo al enviar el correo a " + toEmail + ": " + e.getMessage());
        }
    }

    private String buildPasswordResetHtml(String resetLink) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 10px; overflow: hidden;\">"
                + "<div style=\"background-color: #161c28; padding: 20px; text-align: center;\">"
                + "<h1 style=\"color: #ffffff; margin: 0; font-size: 24px;\">EcoNexo</h1>"
                + "</div>"
                + "<div style=\"padding: 30px; color: #374151; background-color: #ffffff;\">"
                + "<h2 style=\"color: #161c28; font-size: 20px;\">Recuperación de contraseña</h2>"
                + "<p style=\"font-size: 16px; line-height: 1.5;\">Recibimos una solicitud para restablecer la contraseña de tu cuenta. Si fuiste vos, hacé clic en el siguiente botón (el enlace es válido por 15 minutos):</p>"
                + "<div style=\"text-align: center; margin: 30px 0;\">"
                + "<a href=\"" + resetLink + "\" style=\"background-color: #eb5c0c; color: #ffffff; text-decoration: none; padding: 12px 25px; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block;\">Restablecer Contraseña</a>"
                + "</div>"
                + "<p style=\"font-size: 14px; line-height: 1.5; color: #6b7280;\">Si no solicitaste este cambio, podés ignorar este correo de forma segura. Tu cuenta está protegida.</p>"
                + "</div>"
                + "</div>";
    }

    private String buildHtmlContent(String name, String role) {
        String roleName = translateRole(role);
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 10px; overflow: hidden;\">"
                + "<div style=\"background-color: #161c28; padding: 20px; text-align: center;\">"
                + "<h1 style=\"color: #ffffff; margin: 0; font-size: 24px;\">EcoNexo</h1>"
                + "</div>"
                + "<div style=\"padding: 30px; color: #374151; background-color: #ffffff;\">"
                + "<h2 style=\"color: #161c28; font-size: 20px;\">¡Hola, " + name + "!</h2>"
                + "<p style=\"font-size: 16px; line-height: 1.5;\">Nos alegra informarte que tu perfil como <strong>" + roleName + "</strong> ha sido verificado y aprobado por nuestro equipo de administración.</p>"
                + "<p style=\"font-size: 16px; line-height: 1.5;\">Ya podés acceder a tu panel de control y comenzar a operar en la plataforma.</p>"
                + "<div style=\"text-align: center; margin: 30px 0;\">"
                + "<a href=\"http://localhost:4200/login\" style=\"background-color: #eb5c0c; color: #ffffff; text-decoration: none; padding: 12px 25px; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block;\">Iniciar Sesión</a>"
                + "</div>"
                + "</div>"
                + "<div style=\"background-color: #f9fafb; padding: 15px; text-align: center; font-size: 12px; color: #6b7280; border-top: 1px solid #e5e7eb;\">"
                + "Este es un mensaje automático, por favor no respondas a este correo.<br>EcoNexo - Red de rescate de alimentos."
                + "</div>"
                + "</div>";
    }

    private String translateRole(String role) {
        if(role.contains("DONOR")) return "Comercio Donante";
        if(role.contains("DRIVER")) return "Conductor Logístico";
        if(role.contains("NGO")) return "Organización Social";
        return "Usuario";
    }
}
