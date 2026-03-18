package com.btg.prueba_tecnica_seti.repository;

import com.btg.prueba_tecnica_seti.entity.Fondo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FondoRepository extends MongoRepository<Fondo, String> {
}
