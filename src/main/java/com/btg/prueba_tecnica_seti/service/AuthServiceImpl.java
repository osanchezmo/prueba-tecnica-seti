package com.btg.prueba_tecnica_seti.service;

import com.btg.prueba_tecnica_seti.dto.LoginRequest;
import com.btg.prueba_tecnica_seti.dto.LoginResponse;
import com.btg.prueba_tecnica_seti.entity.Cliente;
import com.btg.prueba_tecnica_seti.exception.BadRequestException;
import com.btg.prueba_tecnica_seti.repository.ClienteRepository;
import com.btg.prueba_tecnica_seti.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ClienteRepository clienteRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse autenticar(LoginRequest request) {
        log.info("Intento de autenticación para email: {}", request.getEmail());

        Cliente cliente = clienteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), cliente.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(cliente.getId(), cliente.getEmail(), cliente.getRoles());

        return LoginResponse.builder()
                .token(token)
                .tipo(LoginResponse.TIPO_BEARER)
                .clienteId(cliente.getId())
                .email(cliente.getEmail())
                .roles(cliente.getRoles())
                .build();
    }
}
