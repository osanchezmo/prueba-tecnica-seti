package com.btg.prueba_tecnica_seti.service;

import com.btg.prueba_tecnica_seti.config.MapperConfig;
import com.btg.prueba_tecnica_seti.dto.ClienteRequest;
import com.btg.prueba_tecnica_seti.dto.ClienteResponse;
import com.btg.prueba_tecnica_seti.entity.Cliente;
import com.btg.prueba_tecnica_seti.enums.PreferenciaNotificacion;
import com.btg.prueba_tecnica_seti.exception.BadRequestException;
import com.btg.prueba_tecnica_seti.repository.ClienteRepository;
import com.btg.prueba_tecnica_seti.utils.Constantes;
import com.btg.prueba_tecnica_seti.utils.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {ClienteServiceImpl.class, Util.class, ClienteServiceTest.TestSecurityConfig.class})
@Import({MapperConfig.class})
public class ClienteServiceTest {

    @Configuration
    static class TestSecurityConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        }
    }

    @MockitoBean
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteServiceImpl clienteService;

    private ClienteRequest clienteRequest;
    private Cliente cliente;
    private ClienteResponse clienteResponse;

    @BeforeEach
    void setUp() {

        // Configurar datos de prueba
        clienteRequest = new ClienteRequest();
        clienteRequest.setNombre("Juan Pérez");
        clienteRequest.setEmail("juan.perez@test.com");
        clienteRequest.setTelefono("3001234567");
        clienteRequest.setPreferenciaNotificacion("EMAIL");
        clienteRequest.setPassword("123456");

        cliente = new Cliente();
        cliente.setId("1");
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("juan.perez@test.com");
        cliente.setTelefono("3001234567");
        cliente.setPreferenciaNotificacion(PreferenciaNotificacion.EMAIL);
        cliente.setSaldoDisponible(new BigDecimal("500000"));
        cliente.setRoles(new HashSet<>());

        clienteResponse = new ClienteResponse();
        clienteResponse.setId("1");
        clienteResponse.setNombre("Juan Pérez");
        clienteResponse.setEmail("juan.perez@test.com");
        clienteResponse.setTelefono("3001234567");
        clienteResponse.setPreferenciaNotificacion(PreferenciaNotificacion.EMAIL);
        clienteResponse.setSaldoDisponible(new BigDecimal("500000"));
    }


    @Test
    void crearCliente_DeberiaCrearClienteExitosamente() {

        when(clienteRepository.findByEmail(clienteRequest.getEmail()))
                .thenReturn(Optional.empty());

        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(cliente);


        // When
        ClienteResponse resultado = clienteService.crearCliente(clienteRequest);

        // Then
        assertNotNull(resultado);
        assertEquals(clienteResponse.getId(), resultado.getId());
        assertEquals(clienteResponse.getNombre(), resultado.getNombre());
        assertEquals(clienteResponse.getEmail(), resultado.getEmail());

        // Verificar que se estableció el saldo inicial
        verify(clienteRepository).save(argThat(clienteGuardado ->
                clienteGuardado.getSaldoDisponible().equals(Constantes.SALDO_INICIAL_CLIENTE)
        ));

        // Verificar interacciones
        verify(clienteRepository).findByEmail(clienteRequest.getEmail());
        verify(clienteRepository).save(any(Cliente.class));

        // Then - Verificar resultado completo
        assertNotNull(resultado);
        assertEquals(clienteResponse.getId(), resultado.getId());
        assertEquals(clienteResponse.getNombre(), resultado.getNombre());
        assertEquals(clienteResponse.getEmail(), resultado.getEmail());
        assertEquals(clienteResponse.getTelefono(), resultado.getTelefono());
        assertEquals(clienteResponse.getPreferenciaNotificacion(), resultado.getPreferenciaNotificacion());
        assertEquals(clienteResponse.getSaldoDisponible(), resultado.getSaldoDisponible());
    }

    @Test
    void crearCliente_DeberiaLanzarExcepcion_CuandoEmailYaExiste() {
        // Given
        Cliente clienteExistente = new Cliente();
        clienteExistente.setEmail(clienteRequest.getEmail());

        when(clienteRepository.findByEmail(clienteRequest.getEmail()))
                .thenReturn(Optional.of(clienteExistente));

        // When & Then
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> clienteService.crearCliente(clienteRequest)
        );

        assertEquals("Ya existe un cliente con este email", exception.getMessage());

        // Verificar que no se intentó guardar
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void crearCliente_DeberiaEstablecerPreferenciaNotificacion() {
        // Given
        when(clienteRepository.findByEmail(clienteRequest.getEmail()))
                .thenReturn(Optional.empty());

        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(cliente);


        // When
        clienteService.crearCliente(clienteRequest);

        // Then
        verify(clienteRepository).save(argThat(clienteGuardado ->
                clienteGuardado.getPreferenciaNotificacion() == PreferenciaNotificacion.EMAIL
        ));
    }

}
