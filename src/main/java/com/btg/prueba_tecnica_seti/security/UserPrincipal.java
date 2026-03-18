package com.btg.prueba_tecnica_seti.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class UserPrincipal implements UserDetails {

    private final String clienteId;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(String clienteId, Collection<? extends GrantedAuthority> authorities) {
        this.clienteId = clienteId;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return clienteId;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean tieneRol(String rol) {
        if (authorities == null) return false;
        String roleAuthority = "ROLE_" + rol;
        return authorities.stream()
                .anyMatch(a -> roleAuthority.equalsIgnoreCase(a.getAuthority()));
    }
}
