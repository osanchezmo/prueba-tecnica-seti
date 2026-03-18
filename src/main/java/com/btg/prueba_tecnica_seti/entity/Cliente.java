package com.btg.prueba_tecnica_seti.entity;

import com.btg.prueba_tecnica_seti.enums.PreferenciaNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "clientes")
public class Cliente {

    @Id
    private String id;

    @Field("nombre")
    private String nombre;

    @Field("email")
    private String email;

    @Field("telefono")
    private String telefono;

    @Field("saldo_disponible")
    private BigDecimal saldoDisponible;

    @Field("preferencia_notificacion")
    private PreferenciaNotificacion preferenciaNotificacion;

    @Field("fondos_suscritos")
    @Builder.Default
    private List<String> fondosSuscritos = new ArrayList<>();

    @Field("roles")
    @Builder.Default
    private Set<String> roles = new HashSet<>();
}
