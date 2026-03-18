package com.btg.prueba_tecnica_seti.service;

import com.btg.prueba_tecnica_seti.entity.Cliente;
import com.btg.prueba_tecnica_seti.entity.Fondo;
import com.btg.prueba_tecnica_seti.enums.PreferenciaNotificacion;
import com.btg.prueba_tecnica_seti.service.impl.NotificationService;
import com.btg.prueba_tecnica_seti.utils.EnviarEmail;
import com.btg.prueba_tecnica_seti.utils.SmsInfobip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EnviarEmail enviarEmail;
    private final SmsInfobip smsInfobip;

    @Override
    public void enviarNotificacionSuscripcion(Cliente cliente, Fondo fondo) {
        log.info("Enviando notificación de suscripción para cliente: {}, fondo: {}",
                cliente.getId(), fondo.getNombre());

        String mensaje = String.format(
                "¡Felicitaciones! Se ha suscrito exitosamente al fondo %s por un monto de $%s COP",
                fondo.getNombre(), fondo.getMontoMinimo()
        );

        try {
            if (cliente.getPreferenciaNotificacion() == PreferenciaNotificacion.EMAIL) {
                enviarEmail.enviarEmail(cliente.getEmail(), "Suscripción a Fondo - BTG Pactual", mensaje);
            } else if (cliente.getPreferenciaNotificacion() == PreferenciaNotificacion.SMS) {
                smsInfobip.enviarSms(cliente.getTelefono(), mensaje);
            }

            log.info("Notificación enviada exitosamente");
        } catch (Exception e) {
            log.error("Error enviando notificación: {}", e.getMessage(), e);
            throw e;
        }
    }
}
