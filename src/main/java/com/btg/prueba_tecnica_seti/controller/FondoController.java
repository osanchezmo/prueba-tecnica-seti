package com.btg.prueba_tecnica_seti.controller;

import com.btg.prueba_tecnica_seti.dto.ApiResponse;
import com.btg.prueba_tecnica_seti.dto.FondoSuscripcionCancelacionRequest;
import com.btg.prueba_tecnica_seti.dto.TransaccionResponse;
import com.btg.prueba_tecnica_seti.service.impl.FondoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fondos")
@RequiredArgsConstructor
@Tag(name = "Fondos", description = "API para gestión de fondos de inversión")
@Slf4j
public class FondoController {

    private final FondoService fondoService;

    @PostMapping("/suscribir")
    @Operation(summary = "Suscribirse a un fondo", description = "Permite a un cliente suscribirse a un fondo de inversión")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Suscripción exitosa"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Saldo insuficiente o datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente o fondo no encontrado")
    })
    public ResponseEntity<ApiResponse<TransaccionResponse>> suscribirFondo(@Valid @RequestBody FondoSuscripcionCancelacionRequest request) {
        log.info("Solicitud de suscripción recibida - Cliente: {}, Fondo: {}",
                request.getClienteId(), request.getFondoId());

        TransaccionResponse transaccionId = fondoService.suscribirFondo(request);
        return ResponseEntity.ok(ApiResponse.success("Suscripción exitosa", transaccionId));
    }

    @PostMapping("/cancelar")
    @Operation(summary = "Cancelar suscripción a un fondo", description = "Permite cancelar la suscripción a un fondo y devuelve el monto al cliente")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cancelación exitosa"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "El cliente no está suscrito al fondo"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente o fondo no encontrado")
    })
    public ResponseEntity<ApiResponse<TransaccionResponse>> cancelarSuscripcion(@Valid @RequestBody FondoSuscripcionCancelacionRequest request) {
        log.info("Solicitud de cancelación recibida - Cliente: {}, Fondo: {}",
                request.getClienteId(), request.getFondoId());

        TransaccionResponse transaccion = fondoService.cancelarSuscripcion(request);
        return ResponseEntity.ok(ApiResponse.success("Cancelación exitosa", transaccion));
    }
}

