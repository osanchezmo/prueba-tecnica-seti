package com.btg.prueba_tecnica_seti.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        log.info("Inicializando base de datos con fondos predefinidos...");

    }
}
