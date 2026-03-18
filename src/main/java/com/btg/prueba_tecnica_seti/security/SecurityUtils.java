package com.btg.prueba_tecnica_seti.security;

import com.btg.prueba_tecnica_seti.enums.Roles;
import com.btg.prueba_tecnica_seti.exception.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Usuario no autenticado");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }
        if (principal instanceof String username) {
            return new UserPrincipal(username, authentication.getAuthorities());
        }
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            return new UserPrincipal(userDetails.getUsername(), userDetails.getAuthorities());
        }
        throw new BadRequestException("Usuario no autenticado");
    }

    public static String getClienteIdAutenticado() {
        return getUsuarioAutenticado().getClienteId();
    }

    public static boolean esAdmin() {
        try {
            return getUsuarioAutenticado().tieneRol(Roles.ADMIN.name());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida que el clienteId del request corresponda al usuario autenticado.
     * Los ADMIN pueden operar sobre cualquier cliente.
     * Los CLIENTE solo pueden operar sobre sí mismos.
     */
    public static void validarAccesoCliente(String clienteIdSolicitado) {
        UserPrincipal usuario = getUsuarioAutenticado();
        if (usuario.tieneRol(Roles.ADMIN.name())) {
            return;
        }
        if (!usuario.getClienteId().equals(clienteIdSolicitado)) {
            throw new BadRequestException("No tiene permisos para realizar esta operación sobre otro cliente");
        }
    }
}
