package com.btg.prueba_tecnica_seti.controller;

import com.btg.prueba_tecnica_seti.dto.ApiResponse;
import com.btg.prueba_tecnica_seti.dto.ClienteRequest;
import com.btg.prueba_tecnica_seti.dto.ClienteResponse;
import com.btg.prueba_tecnica_seti.dto.TransaccionResponse;
import com.btg.prueba_tecnica_seti.security.SecurityUtils;
import com.btg.prueba_tecnica_seti.service.impl.ClienteService;
import com.btg.prueba_tecnica_seti.service.impl.FondoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para gestión de fondos de inversión")
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;
    private final FondoService fondoService;

    @PostMapping("/")
    @Operation(summary = "Crear nuevo cliente", description = "Crea un nuevo cliente con saldo inicial de $500.000 COP")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ApiResponse<ClienteResponse>> crearCliente(@Valid @RequestBody ClienteRequest request) {

        log.info("Solicitud de creación de cliente recibida: {}", request.getNombre());

        ClienteResponse cliente = clienteService.crearCliente(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cliente creado exitosamente", cliente));
    }


    @GetMapping("/{clienteId}/transacciones")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(summary = "Obtener historial de transacciones", description = "Obtiene el historial completo de transacciones de un cliente. CLIENTE solo puede ver sus propias transacciones.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos para acceder a este recurso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ApiResponse<List<TransaccionResponse>>> obtenerHistorialTransacciones(
            @Parameter(description = "ID del cliente") @PathVariable String clienteId) {

        SecurityUtils.validarAccesoCliente(clienteId);
        log.info("Solicitud de historial de transacciones para cliente: {}", clienteId);

        List<TransaccionResponse> transacciones = fondoService.obtenerHistorialTransacciones(clienteId);
        return ResponseEntity.ok(ApiResponse.success("Historial obtenido exitosamente", transacciones));
    }
}
