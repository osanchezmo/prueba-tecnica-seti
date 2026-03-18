package com.btg.prueba_tecnica_seti.service;

import com.btg.prueba_tecnica_seti.entity.Cliente;
import com.btg.prueba_tecnica_seti.entity.Fondo;
import com.btg.prueba_tecnica_seti.enums.PreferenciaNotificacion;
import com.btg.prueba_tecnica_seti.utils.EnviarEmail;
import com.btg.prueba_tecnica_seti.utils.SmsInfobip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private EnviarEmail enviarEmail;

    @Mock
    private SmsInfobip smsInfobip;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Cliente cliente;
    private Fondo fondo;
    private String mensajeEsperado;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId("cliente1");
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("juan.perez@test.com");
        cliente.setTelefono("3001234567");

        fondo = new Fondo();
        fondo.setId("fondo1");
        fondo.setNombre("Fondo de Inversión BTG");
        fondo.setMontoMinimo(new BigDecimal("75000"));

        mensajeEsperado = String.format(
                "¡Felicitaciones! Se ha suscrito exitosamente al fondo %s por un monto de $%s COP",
                fondo.getNombre(), fondo.getMontoMinimo()
        );
    }

    @Test
    void enviarNotificacionSuscripcion_DeberiaEnviarEmail_CuandoPreferenciaEsEmail() {

        cliente.setPreferenciaNotificacion(PreferenciaNotificacion.EMAIL);
        doNothing().when(enviarEmail).enviarEmail(anyString(), anyString(), anyString());

        notificationService.enviarNotificacionSuscripcion(cliente, fondo);

        verify(enviarEmail).enviarEmail(
                eq(cliente.getEmail()),
                eq("Suscripción a Fondo - BTG Pactual"),
                eq(mensajeEsperado)
        );
        verify(smsInfobip, never()).enviarSms(anyString(), anyString());
    }

    @Test
    void enviarNotificacionSuscripcion_DeberiaEnviarSms_CuandoPreferenciaEsSms() {

        cliente.setPreferenciaNotificacion(PreferenciaNotificacion.SMS);
        doNothing().when(smsInfobip).enviarSms(anyString(), anyString());

        notificationService.enviarNotificacionSuscripcion(cliente, fondo);

        verify(smsInfobip).enviarSms(
                eq(cliente.getTelefono()),
                eq(mensajeEsperado)
        );
        verify(enviarEmail, never()).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void enviarNotificacionSuscripcion_NoDeberiaEnviarNada_CuandoPreferenciaEsNull() {
        cliente.setPreferenciaNotificacion(null);

        notificationService.enviarNotificacionSuscripcion(cliente, fondo);

        verify(enviarEmail, never()).enviarEmail(anyString(), anyString(), anyString());
        verify(smsInfobip, never()).enviarSms(anyString(), anyString());
    }

    @Test
    void enviarNotificacionSuscripcion_DeberiaLanzarExcepcion_CuandoFallaEnvioEmail() {
        cliente.setPreferenciaNotificacion(PreferenciaNotificacion.EMAIL);
        RuntimeException excepcionEsperada = new RuntimeException("Error al enviar email");
        doThrow(excepcionEsperada).when(enviarEmail).enviarEmail(anyString(), anyString(), anyString());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> notificationService.enviarNotificacionSuscripcion(cliente, fondo)
        );

        assertEquals("Error al enviar email", exception.getMessage());
        verify(enviarEmail).enviarEmail(
                eq(cliente.getEmail()),
                eq("Suscripción a Fondo - BTG Pactual"),
                eq(mensajeEsperado)
        );
    }

    @Test
    void enviarNotificacionSuscripcion_DeberiaLanzarExcepcion_CuandoFallaEnvioSms() {

        cliente.setPreferenciaNotificacion(PreferenciaNotificacion.SMS);
        RuntimeException excepcionEsperada = new RuntimeException("Error al enviar SMS");
        doThrow(excepcionEsperada).when(smsInfobip).enviarSms(anyString(), anyString());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> notificationService.enviarNotificacionSuscripcion(cliente, fondo)
        );

        assertEquals("Error al enviar SMS", exception.getMessage());
        verify(smsInfobip).enviarSms(
                eq(cliente.getTelefono()),
                eq(mensajeEsperado)
        );
    }

    @Test
    void enviarNotificacionSuscripcion_DeberiaCrearMensajeCorrectamente() {
        cliente.setPreferenciaNotificacion(PreferenciaNotificacion.EMAIL);

        notificationService.enviarNotificacionSuscripcion(cliente, fondo);

        verify(enviarEmail).enviarEmail(
                eq(cliente.getEmail()),
                eq("Suscripción a Fondo - BTG Pactual"),
                contains("¡Felicitaciones!")
        );
        verify(enviarEmail).enviarEmail(
                eq(cliente.getEmail()),
                eq("Suscripción a Fondo - BTG Pactual"),
                contains(fondo.getNombre())
        );
        verify(enviarEmail).enviarEmail(
                eq(cliente.getEmail()),
                eq("Suscripción a Fondo - BTG Pactual"),
                contains(fondo.getMontoMinimo().toString())
        );
    }

    @Test
    void enviarNotificacionSuscripcion_DeberiaUsarDatosCorrectos_ParaEmail() {
        cliente.setPreferenciaNotificacion(PreferenciaNotificacion.EMAIL);
        cliente.setEmail("test@example.com");

        Fondo fondoEspecifico = new Fondo();
        fondoEspecifico.setNombre("Fondo Específico");
        fondoEspecifico.setMontoMinimo(new BigDecimal("100000"));

        String mensajeEspecifico = String.format(
                "¡Felicitaciones! Se ha suscrito exitosamente al fondo %s por un monto de $%s COP",
                fondoEspecifico.getNombre(), fondoEspecifico.getMontoMinimo()
        );

        notificationService.enviarNotificacionSuscripcion(cliente, fondoEspecifico);

        verify(enviarEmail).enviarEmail(
                eq("test@example.com"),
                eq("Suscripción a Fondo - BTG Pactual"),
                eq(mensajeEspecifico)
        );
    }

    @Test
    void enviarNotificacionSuscripcion_DeberiaUsarDatosCorrectos_ParaSms() {
        cliente.setPreferenciaNotificacion(PreferenciaNotificacion.SMS);
        cliente.setTelefono("3009876543");

        Fondo fondoEspecifico = new Fondo();
        fondoEspecifico.setNombre("Fondo Test SMS");
        fondoEspecifico.setMontoMinimo(new BigDecimal("50000"));

        String mensajeEspecifico = String.format(
                "¡Felicitaciones! Se ha suscrito exitosamente al fondo %s por un monto de $%s COP",
                fondoEspecifico.getNombre(), fondoEspecifico.getMontoMinimo()
        );

        notificationService.enviarNotificacionSuscripcion(cliente, fondoEspecifico);

        verify(smsInfobip).enviarSms(
                eq("3009876543"),
                eq(mensajeEspecifico)
        );
    }
}