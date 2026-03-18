package com.btg.prueba_tecnica_seti.service.impl;

import com.btg.prueba_tecnica_seti.entity.Cliente;
import com.btg.prueba_tecnica_seti.entity.Fondo;

public interface NotificationService {
    void enviarNotificacionSuscripcion(Cliente cliente, Fondo fondo);
}
