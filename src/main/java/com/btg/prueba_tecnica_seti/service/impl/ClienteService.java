package com.btg.prueba_tecnica_seti.service.impl;

import com.btg.prueba_tecnica_seti.dto.ClienteRequest;
import com.btg.prueba_tecnica_seti.dto.ClienteResponse;

public interface ClienteService {
    ClienteResponse crearCliente(ClienteRequest request);
}

