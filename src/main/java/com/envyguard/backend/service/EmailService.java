package com.envyguard.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendCredentials(String to, String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenido a EnvyGuard - Tus credenciales de acceso");
        message.setText(String.format("""
            ¡Bienvenido a EnvyGuard!
            
            Tus credenciales de acceso son:
            Email: %s
            Contraseña: %s
            
            Por favor, cambia tu contraseña después de tu primer inicio de sesión.
            
            Saludos,
            El equipo de EnvyGuard
            """, email, password));

        mailSender.send(message);
    }
}