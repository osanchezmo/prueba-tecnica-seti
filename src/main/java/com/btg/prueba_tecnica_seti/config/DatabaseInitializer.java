package com.btg.prueba_tecnica_seti.config;

import com.btg.prueba_tecnica_seti.entity.Fondo;
import com.btg.prueba_tecnica_seti.repository.FondoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final FondoRepository fondoRepository;

    @Override
    public void run(String... args) throws Exception {

        log.info("Inicializando base de datos con fondos predefinidos...");

        if (fondoRepository.count() > 0) {
            log.info("Los fondos ya están inicializados en la base de datos");
            return;
        }

        List<Fondo> fondosPredefinidos = Arrays.asList(
                new Fondo("1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV"),
                new Fondo("2", "FPV_BTG_PACTUAL_ECOPETROL", new BigDecimal("125000"), "FPV"),
                new Fondo("3", "DEUDAPRIVADA", new BigDecimal("50000"), "FIC"),
                new Fondo("4", "FDO-ACCIONES", new BigDecimal("250000"), "FIC"),
                new Fondo("5", "FPV_BTG_PACTUAL_DINAMICA", new BigDecimal("100000"), "FPV")
        );

        fondoRepository.saveAll(fondosPredefinidos);
        log.info("Se han inicializado {} fondos en la base de datos", fondosPredefinidos.size());
    }
}
