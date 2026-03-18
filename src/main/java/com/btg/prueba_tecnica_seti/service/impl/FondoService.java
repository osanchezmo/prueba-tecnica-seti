package com.btg.prueba_tecnica_seti.service.impl;

import com.btg.prueba_tecnica_seti.dto.FondoSuscripcionCancelacionRequest;
import com.btg.prueba_tecnica_seti.dto.TransaccionResponse;

import java.util.List;

public interface FondoService {
    TransaccionResponse suscribirFondo(FondoSuscripcionCancelacionRequest request);
    TransaccionResponse cancelarSuscripcion(FondoSuscripcionCancelacionRequest request);
    List<TransaccionResponse> obtenerHistorialTransacciones(String clienteId);
}
