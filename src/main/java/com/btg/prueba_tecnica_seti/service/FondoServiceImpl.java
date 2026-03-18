package com.btg.prueba_tecnica_seti.service;

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
import com.btg.prueba_tecnica_seti.service.impl.FondoService;
import com.btg.prueba_tecnica_seti.service.impl.NotificationService;
import com.btg.prueba_tecnica_seti.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FondoServiceImpl implements FondoService {

    private final FondoRepository fondoRepository;
    private final ClienteRepository clienteRepository;
    private final TransaccionRepository transaccionRepository;
    private final Util util;
    private final NotificationService notificationService;

    @Override
    public TransaccionResponse suscribirFondo(FondoSuscripcionCancelacionRequest request) {

        log.info("Procesando suscripción de fondo - Cliente: {}, Fondo: {}",
                request.getClienteId(), request.getFondoId());


        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        Fondo fondo = fondoRepository.findById(request.getFondoId())
                .orElseThrow(() -> new NotFoundException("Fondo no encontrado"));

        // Verificar si ya está suscrito
        if (cliente.getFondosSuscritos() != null &&
                cliente.getFondosSuscritos().contains(request.getFondoId())) {
            throw new BadRequestException("El cliente ya está suscrito a este fondo");
        }

        // Verificar saldo suficiente
        if (cliente.getSaldoDisponible().compareTo(fondo.getMontoMinimo()) < 0) {
            throw new BadRequestException(
                    String.format("No tiene saldo disponible para vincularse al fondo %s", fondo.getNombre())
            );
        }

        // Actualizar saldo del cliente
        BigDecimal nuevoSaldo = cliente.getSaldoDisponible().subtract(fondo.getMontoMinimo());
        cliente.setSaldoDisponible(nuevoSaldo);

        // Agregar fondo a la lista de suscritos
        if (cliente.getFondosSuscritos() == null) {
            cliente.setFondosSuscritos(new ArrayList<>());
        }
        cliente.getFondosSuscritos().add(request.getFondoId());

        // Guardar cliente actualizado
        clienteRepository.save(cliente);

        // Crear transacción
        Transaccion transactionGuardada = transaccionRepository.save(Transaccion
                .builder()
                .clienteId(cliente.getId())
                .fondoId(fondo.getId())
                .monto(fondo.getMontoMinimo())
                .tipo(TipoTransaccion.APERTURA)
                .fechaTransaccion(LocalDateTime.now())
                .build());

        TransaccionResponse transaction = util.convertTo(transactionGuardada, TransaccionResponse.class);
        transaction.setNombreFondo(fondo.getNombre());

        // Enviar notificación
        try {
            notificationService.enviarNotificacionSuscripcion(cliente, fondo);
        } catch (Exception e) {
            log.warn("Error enviando notificación: {}", e.getMessage());
        }


        return transaction;
    }

    @Override
    public TransaccionResponse cancelarSuscripcion(FondoSuscripcionCancelacionRequest request) {

        log.info("Procesando cancelación de suscripción - Cliente: {}, Fondo: {}",
                request.getClienteId(), request.getFondoId());

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        Fondo fondo = fondoRepository.findById(request.getFondoId())
                .orElseThrow(() -> new NotFoundException("Fondo no encontrado"));

        // Verificar si está suscrito
        if (cliente.getFondosSuscritos() == null ||
                !cliente.getFondosSuscritos().contains(request.getFondoId())) {
            throw new BadRequestException("El cliente no está suscrito a este fondo");
        }

        // Devolver el monto al saldo del cliente
        BigDecimal nuevoSaldo = cliente.getSaldoDisponible().add(fondo.getMontoMinimo());
        cliente.setSaldoDisponible(nuevoSaldo);
        cliente.getFondosSuscritos().remove(request.getFondoId());
        clienteRepository.save(cliente);

        // Crear transacción
        Transaccion transactionGuardada = transaccionRepository.save(Transaccion
                .builder()
                .clienteId(cliente.getId())
                .fondoId(fondo.getId())
                .monto(fondo.getMontoMinimo())
                .tipo(TipoTransaccion.CANCELACION)
                .fechaTransaccion(LocalDateTime.now())
                .build());

        log.info("Cancelación exitosa - Transacción ID: {}", transactionGuardada.getId());

        TransaccionResponse transaction = util.convertTo(transactionGuardada, TransaccionResponse.class);
        transaction.setNombreFondo(fondo.getNombre());

        return transaction;
    }

    @Override
    public List<TransaccionResponse> obtenerHistorialTransacciones(String clienteId) {

        log.info("Obteniendo historial de transacciones para cliente: {}", clienteId);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        List<Transaccion> transacciones = transaccionRepository.findByClienteIdOrderByFechaTransaccionDesc(clienteId);

        return transacciones.stream()
                .map(this::mapearTransaccionAResponse)
                .collect(Collectors.toList());
    }

    private TransaccionResponse mapearTransaccionAResponse(Transaccion transaccion) {

        Optional<Fondo> fondo = fondoRepository.findById(transaccion.getFondoId());
        String nombreFondo = fondo.map(Fondo::getNombre).orElse("Fondo no encontrado");


        return TransaccionResponse
                .builder()
                .id(transaccion.getId())
                .clienteId(transaccion.getClienteId())
                .fondoId(transaccion.getFondoId())
                .nombreFondo(nombreFondo)
                .tipo(transaccion.getTipo())
                .monto(transaccion.getMonto())
                .fechaTransaccion(transaccion.getFechaTransaccion())
                .build();
    }
}
