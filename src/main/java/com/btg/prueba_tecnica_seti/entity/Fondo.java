package com.btg.prueba_tecnica_seti.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "fondos")
public class Fondo {

    @Id
    private String id;

    @Field("nombre")
    private String nombre;

    @Field("monto_minimo")
    private BigDecimal montoMinimo;

    @Field("categoria")
    private String categoria;
}
