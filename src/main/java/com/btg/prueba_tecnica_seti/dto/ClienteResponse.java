package com.btg.prueba_tecnica_seti.dto;

import com.btg.prueba_tecnica_seti.enums.PreferenciaNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponse {
    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private BigDecimal saldoDisponible;
    private PreferenciaNotificacion preferenciaNotificacion;
    private List<String> fondosSuscritos = new ArrayList<>();
}
