package com.btg.prueba_tecnica_seti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo;
    private String clienteId;
    private String email;
    private Set<String> roles;

    public static final String TIPO_BEARER = "Bearer";
}
