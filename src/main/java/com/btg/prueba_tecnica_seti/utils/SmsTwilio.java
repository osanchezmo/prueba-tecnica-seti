package com.btg.prueba_tecnica_seti.utils;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsTwilio {

    @Value("${twilio.phone-number}")
    private String phoneNumber;

    @Value("${twilio.country-code:57}")
    private String countryCode;

    public void enviarSms(String to, String text) {
        String toFormatted = formatPhoneNumber(to);

        try {
            Message message = Message.creator(
                    new PhoneNumber(toFormatted),
                    new PhoneNumber(phoneNumber),
                    text
            ).create();

            log.info("SMS enviado correctamente - SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("Error enviando SMS a {}: {}", toFormatted, e.getMessage());
            throw new RuntimeException("Error al enviar SMS: " + e.getMessage(), e);
        }
    }

    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Número de teléfono no puede ser vacío");
        }
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.startsWith("57") && cleaned.length() >= 10) {
            return "+" + cleaned;
        }
        if (!phone.startsWith("+")) {
            return "+" + countryCode + cleaned;
        }
        return phone;
    }
}
