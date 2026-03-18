package com.btg.prueba_tecnica_seti.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnviarEmail {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notifications.email.from}")
    private String emailFrom;

    public void enviarEmail(String destinatario, String asunto, String mensaje) {

        log.info("Enviando email a: {}", destinatario);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(destinatario);
            message.setSubject(asunto);
            message.setText(mensaje);

            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", destinatario, e.getMessage(), e);
            throw new RuntimeException("Error enviando email", e);
        }
    }
}
