package com.btg.prueba_tecnica_seti.service;


import com.btg.prueba_tecnica_seti.config.MapperConfig;
import com.btg.prueba_tecnica_seti.dto.FondoSuscripcionCancelacionRequest;
import com.btg.prueba_tecnica_seti.dto.TransaccionResponse;
import com.btg.prueba_tecnica_seti.entity.Cliente;
import com.btg.prueba_tecnica_seti.entity.Fondo;
import com.btg.prueba_tecnica_seti.entity.Transaccion;
import com.btg.prueba_tecnica_seti.enums.TipoTransaccion;
import com.btg.prueba_tecnica_seti.exception.BadRequestException;
import com.btg.prueba_tecnica_seti.exception.NotFoundException;
import com.btg.prueba_tecnica_seti.repository.ClienteRepository;
import com.btg.prueba_tecnica_seti.repository.FondoRepository;
import com.btg.prueba_tecnica_seti.repository.TransaccionRepository;
import com.btg.prueba_tecnica_seti.service.impl.NotificationService;
import com.btg.prueba_tecnica_seti.utils.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringJUnitConfig({ FondoServiceImpl.class, Util.class})
@Import({MapperConfig.class})
public class FondoServiceTest {

    @MockitoBean
    private FondoRepository fondoRepository;

    @MockitoBean
    private ClienteRepository clienteRepository;

    @MockitoBean
    private TransaccionRepository transaccionRepository;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private FondoServiceImpl fondoService;


    private FondoSuscripcionCancelacionRequest request;
    private Cliente cliente;
    private Fondo fondo;
    private Transaccion transaccion;
    private TransaccionResponse transaccionResponse;

    @BeforeEach
    void setUp() {
        request = new FondoSuscripcionCancelacionRequest();
        request.setClienteId("cliente1");
        request.setFondoId("fondo1");

        cliente = new Cliente();
        cliente.setId("cliente1");
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("juan@test.com");
        cliente.setSaldoDisponible(new BigDecimal("600000"));
        cliente.setFondosSuscritos(new ArrayList<>());

        fondo = new Fondo();
        fondo.setId("fondo1");
        fondo.setNombre("Fondo de Inversión BTG");
        fondo.setMontoMinimo(new BigDecimal("75000"));

        transaccion = new Transaccion();
        transaccion.setId("trans1");
        transaccion.setClienteId("cliente1");
        transaccion.setFondoId("fondo1");
        transaccion.setMonto(new BigDecimal("75000"));
        transaccion.setTipo(TipoTransaccion.APERTURA);
        transaccion.setFechaTransaccion(LocalDateTime.now());

        transaccionResponse = new TransaccionResponse();
        transaccionResponse.setId("trans1");
        transaccionResponse.setClienteId("cliente1");
        transaccionResponse.setFondoId("fondo1");
        transaccionResponse.setNombreFondo("Fondo de Inversión BTG");
        transaccionResponse.setMonto(new BigDecimal("75000"));
        transaccionResponse.setTipo(TipoTransaccion.APERTURA);
        transaccionResponse.setFechaTransaccion(LocalDateTime.now());
    }

    @Test
    void suscribirFondo_DeberiaCrearSuscripcionExitosamente() {

        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.of(cliente));

        when(fondoRepository.findById(request.getFondoId()))
                .thenReturn(Optional.of(fondo));

        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(cliente);

        when(transaccionRepository.save(any(Transaccion.class)))
                .thenReturn(transaccion);

        TransaccionResponse resultado = fondoService.suscribirFondo(request);

        assertNotNull(resultado);
        assertEquals(transaccionResponse.getId(), resultado.getId());
        assertEquals(transaccionResponse.getClienteId(), resultado.getClienteId());
        assertEquals(transaccionResponse.getFondoId(), resultado.getFondoId());
        assertEquals(fondo.getNombre(), resultado.getNombreFondo());
        assertEquals(transaccionResponse.getTipo(), resultado.getTipo());
        assertEquals(transaccionResponse.getMonto(), resultado.getMonto());
        assertEquals(TipoTransaccion.APERTURA, resultado.getTipo());


        verify(clienteRepository).save(argThat(clienteActualizado -> {
            BigDecimal saldoEsperado = new BigDecimal("600000").subtract(new BigDecimal("75000"));
            return clienteActualizado.getSaldoDisponible().equals(saldoEsperado);
        }));


        verify(clienteRepository).save(argThat(clienteActualizado ->
                clienteActualizado.getFondosSuscritos().contains(request.getFondoId())
        ));


        verify(transaccionRepository).save(argThat(trans ->
                trans.getTipo() == TipoTransaccion.APERTURA &&
                        trans.getClienteId().equals(request.getClienteId()) &&
                        trans.getFondoId().equals(request.getFondoId()) &&
                        trans.getMonto().equals(fondo.getMontoMinimo())
        ));

        verify(notificationService).enviarNotificacionSuscripcion(cliente, fondo);
    }


    @Test
    void suscribirFondo_DeberiaLanzarExcepcion_CuandoClienteNoExiste() {

        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fondoService.suscribirFondo(request)
        );

        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(fondoRepository, never()).findById(any());
    }

    @Test
    void suscribirFondo_DeberiaLanzarExcepcion_CuandoFondoNoExiste() {

        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.of(cliente));
        when(fondoRepository.findById(request.getFondoId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fondoService.suscribirFondo(request)
        );

        assertEquals("Fondo no encontrado", exception.getMessage());
    }

    @Test
    void suscribirFondo_DeberiaLanzarExcepcion_CuandoYaEstaSuscrito() {

        cliente.getFondosSuscritos().add(request.getFondoId());

        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.of(cliente));
        when(fondoRepository.findById(request.getFondoId()))
                .thenReturn(Optional.of(fondo));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> fondoService.suscribirFondo(request)
        );

        assertEquals("El cliente ya está suscrito a este fondo", exception.getMessage());
    }

    @Test
    void suscribirFondo_DeberiaLanzarExcepcion_CuandoSaldoInsuficiente() {

        cliente.setSaldoDisponible(new BigDecimal("50000")); // Menor al monto mínimo

        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.of(cliente));
        when(fondoRepository.findById(request.getFondoId()))
                .thenReturn(Optional.of(fondo));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> fondoService.suscribirFondo(request)
        );

        String mensajeEsperado = String.format(
                "No tiene saldo disponible para vincularse al fondo %s",
                fondo.getNombre()
        );
        assertEquals(mensajeEsperado, exception.getMessage());
    }

    @Test
    void suscribirFondo_DeberiaInicializarListaFondosSuscritos_CuandoEsNull() {

        cliente.setFondosSuscritos(null);

        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.of(cliente));
        when(fondoRepository.findById(request.getFondoId()))
                .thenReturn(Optional.of(fondo));
        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(cliente);
        when(transaccionRepository.save(any(Transaccion.class)))
                .thenReturn(transaccion);

        fondoService.suscribirFondo(request);

        verify(clienteRepository).save(argThat(clienteActualizado -> {
            assertNotNull(clienteActualizado.getFondosSuscritos());
            assertTrue(clienteActualizado.getFondosSuscritos().contains(request.getFondoId()));
            return true;
        }));
    }

    @Test
    void cancelarSuscripcion_DeberiaRealizarCancelacionExitosamente() {

        cliente.getFondosSuscritos().add(request.getFondoId());
        transaccion.setTipo(TipoTransaccion.CANCELACION);
        transaccionResponse.setTipo(TipoTransaccion.CANCELACION);

        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.of(cliente));
        when(fondoRepository.findById(request.getFondoId()))
                .thenReturn(Optional.of(fondo));
        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(cliente);
        when(transaccionRepository.save(any(Transaccion.class)))
                .thenReturn(transaccion);

        TransaccionResponse resultado = fondoService.cancelarSuscripcion(request);

        assertNotNull(resultado);
        assertEquals(transaccionResponse.getId(), resultado.getId());
        assertEquals(transaccionResponse.getClienteId(), resultado.getClienteId());
        assertEquals(transaccionResponse.getFondoId(), resultado.getFondoId());
        assertEquals(fondo.getNombre(), resultado.getNombreFondo());
        assertEquals(TipoTransaccion.CANCELACION, resultado.getTipo());
        assertEquals(transaccionResponse.getMonto(), resultado.getMonto());

        verify(clienteRepository).save(argThat(clienteActualizado -> {
            BigDecimal saldoEsperado = new BigDecimal("600000").add(new BigDecimal("75000"));
            return clienteActualizado.getSaldoDisponible().equals(saldoEsperado);
        }));

        verify(clienteRepository).save(argThat(clienteActualizado ->
                !clienteActualizado.getFondosSuscritos().contains(request.getFondoId())
        ));

        verify(transaccionRepository).save(argThat(trans ->
                trans.getTipo() == TipoTransaccion.CANCELACION &&
                        trans.getClienteId().equals(request.getClienteId()) &&
                        trans.getFondoId().equals(request.getFondoId()) &&
                        trans.getMonto().equals(fondo.getMontoMinimo())
        ));
    }

    @Test
    void cancelarSuscripcion_DeberiaLanzarExcepcion_CuandoNoEstaSuscrito() {

        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.of(cliente));
        when(fondoRepository.findById(request.getFondoId()))
                .thenReturn(Optional.of(fondo));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> fondoService.cancelarSuscripcion(request)
        );

        assertEquals("El cliente no está suscrito a este fondo", exception.getMessage());
    }

    @Test
    void cancelarSuscripcion_DeberiaLanzarExcepcion_CuandoListaSuscripcionesEsNull() {

        cliente.setFondosSuscritos(null);

        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.of(cliente));
        when(fondoRepository.findById(request.getFondoId()))
                .thenReturn(Optional.of(fondo));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> fondoService.cancelarSuscripcion(request)
        );

        assertEquals("El cliente no está suscrito a este fondo", exception.getMessage());
    }

    @Test
    void obtenerHistorialTransacciones_DeberiaRetornarListaTransacciones() {

        String clienteId = "cliente1";
        List<Transaccion> transacciones = Arrays.asList(transaccion);

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));
        when(transaccionRepository.findByClienteIdOrderByFechaTransaccionDesc(clienteId))
                .thenReturn(transacciones);
        when(fondoRepository.findById(transaccion.getFondoId()))
                .thenReturn(Optional.of(fondo));

        List<TransaccionResponse> resultado = fondoService.obtenerHistorialTransacciones(clienteId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        TransaccionResponse transaccionRespuesta = resultado.get(0);
        assertEquals(transaccion.getId(), transaccionRespuesta.getId());
        assertEquals(transaccion.getClienteId(), transaccionRespuesta.getClienteId());
        assertEquals(transaccion.getFondoId(), transaccionRespuesta.getFondoId());
        assertEquals(fondo.getNombre(), transaccionRespuesta.getNombreFondo());
        assertEquals(transaccion.getTipo(), transaccionRespuesta.getTipo());
        assertEquals(transaccion.getMonto(), transaccionRespuesta.getMonto());
        assertEquals(transaccion.getFechaTransaccion(), transaccionRespuesta.getFechaTransaccion());

        verify(clienteRepository).findById(clienteId);
        verify(transaccionRepository).findByClienteIdOrderByFechaTransaccionDesc(clienteId);
        verify(fondoRepository).findById(transaccion.getFondoId());
    }


    @Test
    void obtenerHistorialTransacciones_DeberiaLanzarExcepcion_CuandoClienteNoExiste() {

        String clienteId = "cliente-inexistente";

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fondoService.obtenerHistorialTransacciones(clienteId)
        );

        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    void obtenerHistorialTransacciones_DeberiaRetornarListaVacia_CuandoNoHayTransacciones() {

        String clienteId = "cliente1";
        List<Transaccion> transaccionesVacias = new ArrayList<>();

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));
        when(transaccionRepository.findByClienteIdOrderByFechaTransaccionDesc(clienteId))
                .thenReturn(transaccionesVacias);

        List<TransaccionResponse> resultado = fondoService.obtenerHistorialTransacciones(clienteId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void suscribirFondo_DeberiaTomarLogsDeErrorNotificacion_CuandoFallaNotificacion() {
        // Given
        when(clienteRepository.findById(request.getClienteId()))
                .thenReturn(Optional.of(cliente));
        when(fondoRepository.findById(request.getFondoId()))
                .thenReturn(Optional.of(fondo));
        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(cliente);
        when(transaccionRepository.save(any(Transaccion.class)))
                .thenReturn(transaccion);

        // Simular excepción en notificación
        doThrow(new RuntimeException("Error de notificación"))
                .when(notificationService).enviarNotificacionSuscripcion(cliente, fondo);

        // When
        TransaccionResponse resultado = fondoService.suscribirFondo(request);

        // Then
        assertNotNull(resultado); // La transacción debe completarse aunque falle la notificación
        verify(notificationService).enviarNotificacionSuscripcion(cliente, fondo);
    }

}
