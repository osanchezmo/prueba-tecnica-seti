package com.btg.prueba_tecnica_seti.repository;

import com.btg.prueba_tecnica_seti.entity.Transaccion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionRepository extends MongoRepository<Transaccion, String> {
    List<Transaccion> findByClienteIdOrderByFechaTransaccionDesc(String clienteId);

}
