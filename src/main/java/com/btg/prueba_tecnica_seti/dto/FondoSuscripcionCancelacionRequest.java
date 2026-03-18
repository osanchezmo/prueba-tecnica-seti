package com.btg.prueba_tecnica_seti.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FondoSuscripcionCancelacionRequest {

    @NotBlank(message = "El ID del cliente es obligatorio")
    private String clienteId;

    @NotBlank(message = "El ID del fondo es obligatorio")
    private String fondoId;
}
