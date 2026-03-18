package com.btg.prueba_tecnica_seti.service;

import com.btg.prueba_tecnica_seti.dto.ClienteRequest;
import com.btg.prueba_tecnica_seti.dto.ClienteResponse;
import com.btg.prueba_tecnica_seti.entity.Cliente;
import com.btg.prueba_tecnica_seti.enums.PreferenciaNotificacion;
import com.btg.prueba_tecnica_seti.exception.NotFoundException;
import com.btg.prueba_tecnica_seti.repository.ClienteRepository;
import com.btg.prueba_tecnica_seti.service.impl.ClienteService;
import com.btg.prueba_tecnica_seti.utils.Constantes;
import com.btg.prueba_tecnica_seti.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClienteServiceImpl  implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final Util util;

    @Override
    public ClienteResponse crearCliente(ClienteRequest request) {

        log.info("Creando nuevo cliente: {}", request.getNombre());

        Optional<Cliente> clienteExistente = clienteRepository.findByEmail(request.getEmail());
        if (clienteExistente.isPresent()) {
            throw new NotFoundException("Ya existe un cliente con este email");
        }

        Cliente cliente = util.convertTo(request, Cliente.class);
        cliente.setSaldoDisponible(Constantes.SALDO_INICIAL_CLIENTE);
        cliente.setPreferenciaNotificacion(util.validarEnum(PreferenciaNotificacion.class, request.getPreferenciaNotificacion()));
        Cliente clienteGuardado = clienteRepository.save(cliente);
        //cliente.getRoles().add(Roles.CLIENTE.name());

        log.info("Cliente creado exitosamente con ID: {}", clienteGuardado.getId());
        return util.convertTo(clienteGuardado, ClienteResponse.class);
    }
}
