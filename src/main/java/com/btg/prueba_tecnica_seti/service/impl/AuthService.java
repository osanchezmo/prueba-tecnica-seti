package com.btg.prueba_tecnica_seti.service.impl;

import com.btg.prueba_tecnica_seti.dto.LoginRequest;
import com.btg.prueba_tecnica_seti.dto.LoginResponse;

public interface AuthService {
    LoginResponse autenticar(LoginRequest request);
}
