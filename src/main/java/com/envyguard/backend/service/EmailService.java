package com.envyguard.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending emails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    /**
     * Sends an email with account credentials.
     *
     * @param to       Recipient email
     * @param email    User email (username)
     * @param password User password
     */
    public void sendCredentials(String to, String email, String password) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Bienvenido a EnvyGuard - Credenciales de Acceso");
            helper.setText(buildCredentialsEmailHtml(email, password), true);

            mailSender.send(mimeMessage);
            log.info("Credentials sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Error sending credentials to {}: {}", to, e.getMessage());
            throw new RuntimeException("Error sending credentials email", e);
        }
    }

    private String buildCredentialsEmailHtml(String email, String password) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0f0f0f;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color: #0f0f0f; padding: 40px 20px;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="background: linear-gradient(145deg, #1a1a2e 0%%, #16213e 100%%); border-radius: 16px; overflow: hidden; box-shadow: 0 20px 60px rgba(0, 255, 255, 0.1);">
                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #0d7377 0%%, #14ffec 100%%); padding: 40px 30px; text-align: center;">
                                            <h1 style="margin: 0; color: #0f0f0f; font-size: 32px; font-weight: 700; letter-spacing: 2px;">
                                                🛡️ EnvyGuard
                                            </h1>
                                            <p style="margin: 10px 0 0 0; color: #1a1a2e; font-size: 14px; font-weight: 500;">
                                                Sistema de Gestión de Dispositivos
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Welcome Message -->
                                    <tr>
                                        <td style="padding: 40px 30px 20px 30px;">
                                            <h2 style="margin: 0 0 10px 0; color: #14ffec; font-size: 24px; font-weight: 600;">
                                                ¡Bienvenido/a!
                                            </h2>
                                            <p style="margin: 0; color: #e0e0e0; font-size: 16px; line-height: 1.6;">
                                                Tu cuenta ha sido creada exitosamente. A continuación encontrarás tus credenciales de acceso:
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Credentials Box -->
                                    <tr>
                                        <td style="padding: 0 30px 30px 30px;">
                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background: linear-gradient(145deg, #252542 0%%, #1e1e3f 100%%); border-radius: 12px; border-left: 4px solid #14ffec;">
                                                <tr>
                                                    <td style="padding: 25px;">
                                                        <!-- Email -->
                                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom: 15px;">
                                                            <tr>
                                                                <td style="padding: 12px 15px; background-color: #1a1a2e; border-radius: 8px;">
                                                                    <p style="margin: 0 0 5px 0; color: #14ffec; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 1px;">
                                                                        📧 Correo electrónico
                                                                    </p>
                                                                    <p style="margin: 0; color: #ffffff; font-size: 16px; font-weight: 500;">
                                                                        %s
                                                                    </p>
                                                                </td>
                                                            </tr>
                                                        </table>

                                                        <!-- Password -->
                                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <td style="padding: 12px 15px; background-color: #1a1a2e; border-radius: 8px;">
                                                                    <p style="margin: 0 0 5px 0; color: #14ffec; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 1px;">
                                                                        🔐 Contraseña
                                                                    </p>
                                                                    <p style="margin: 0; color: #ffffff; font-size: 16px; font-weight: 500; font-family: 'Courier New', monospace; background-color: #0f0f1a; padding: 8px 12px; border-radius: 4px; display: inline-block;">
                                                                        %s
                                                                    </p>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Important Notice -->
                                    <tr>
                                        <td style="padding: 0 30px 30px 30px;">
                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color: rgba(20, 255, 236, 0.1); border-radius: 8px; border: 1px solid rgba(20, 255, 236, 0.3);">
                                                <tr>
                                                    <td style="padding: 15px 20px;">
                                                        <p style="margin: 0; color: #14ffec; font-size: 14px; line-height: 1.5;">
                                                            💡 <strong>Importante:</strong> Si necesitas cambiar tu contraseña, por favor comunícate con un administrador del sistema.
                                                        </p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color: #0a0a14; padding: 25px 30px; text-align: center; border-top: 1px solid #252542;">
                                            <p style="margin: 0 0 10px 0; color: #888888; font-size: 14px;">
                                                Saludos cordiales,
                                            </p>
                                            <p style="margin: 0; color: #14ffec; font-size: 16px; font-weight: 600;">
                                                El equipo de EnvyGuard
                                            </p>
                                            <p style="margin: 15px 0 0 0; color: #555555; font-size: 12px;">
                                                © 2024 EnvyGuard. Todos los derechos reservados.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(email, password);
    }
}