package com.btg.prueba_tecnica_seti.entity;

import com.btg.prueba_tecnica_seti.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transacciones")
public class Transaccion {

    @Id
    private String id;

    @Field("cliente_id")
    private String clienteId;

    @Field("fondo_id")
    private String fondoId;

    @Field("tipo")
    private TipoTransaccion tipo;

    @Field("monto")
    private BigDecimal monto;

    @CreatedDate
    @Field("fecha_transaccion")
    private LocalDateTime fechaTransaccion;
}
