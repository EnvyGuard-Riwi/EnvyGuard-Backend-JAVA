package com.envyguard.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending email notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Value("${spring.mail.from-name}")
    private String fromName;

    /**
     * Sends a simple text email.
     *
     * @param to Recipient email address
     * @param subject Email subject
     * @param text Email content
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Simple email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Error sending simple email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Sends an HTML email using Thymeleaf template.
     *
     * @param to Recipient email address
     * @param subject Email subject
     * @param template Template name (without .html extension)
     * @param context Template context variables
     */
    public void sendHtmlEmail(String to, String subject, String template, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            
            String htmlContent = templateEngine.process(template, context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("HTML email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Error sending HTML email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    /**
     * Sends welcome email with user credentials.
     *
     * @param to Recipient email address
     * @param firstName User's first name
     * @param email User's email
     * @param password User's password (plain text for first-time login)
     */
    public void sendWelcomeEmail(String to, String firstName, String email, String password) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("email", email);
        context.setVariable("password", password);
        context.setVariable("loginUrl", "http://localhost:8080/api/auth/login");
        
        sendHtmlEmail(to, "Welcome to EnvyGuard - Your Account Credentials", "welcome-email", context);
    }
}
