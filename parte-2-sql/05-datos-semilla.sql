-- Parte 2 — Datos de prueba (schema public)
--
-- Ejecutar conectado a `btg` después de 04-constantes.sql.
-- Re-ejecutar requiere vaciar tablas antes (o borrar y volver a crear).

SET search_path TO public, pg_catalog;

INSERT INTO cliente (id, nombre, apellidos, ciudad) VALUES
    (1, 'Juan Carlos', 'Pérez González', 'Bogotá'),
    (2, 'María Fernanda', 'López Martínez', 'Bogotá'),
    (3, 'Carlos Alberto', 'Rodríguez Silva', 'Medellín'),
    (4, 'Ana Isabel', 'García Herrera', 'Medellín'),
    (5, 'Luis Fernando', 'Martínez Ruiz', 'Cali'),
    (6, 'Patricia', 'Jiménez Morales', 'Cali'),
    (7, 'Roberto', 'Sánchez Torres', 'Barranquilla'),
    (8, 'Carmen', 'Herrera Vásquez', 'Cartagena'),
    (9, 'Diego', 'Morales Castro', 'Bogotá'),
    (10, 'Alejandra', 'Vásquez Duarte', 'Medellín');

INSERT INTO sucursal (id, nombre, ciudad) VALUES
    (1, 'BTG Centro Bogotá', 'Bogotá'),
    (2, 'BTG Norte Medellín', 'Medellín'),
    (3, 'BTG Sur Cali', 'Cali'),
    (4, 'BTG Plaza Barranquilla', 'Barranquilla'),
    (5, 'BTG Centro Cartagena', 'Cartagena');

INSERT INTO producto (id, nombre, tipoProducto) VALUES
    (1, 'FPV_BTG_PACTUAL_RECAUDADORA', 'FPV'),
    (2, 'FPV_BTG_PACTUAL_ECOPETROL', 'FPV'),
    (3, 'DEUDAPRIVADA', 'FIC'),
    (4, 'FDO-ACCIONES', 'FIC'),
    (5, 'FPV_BTG_PACTUAL_DINAMICA', 'FPV'),
    (6, 'PRODUCTO_EXCLUSIVO_BOGOTA', 'EXCLUSIVO'),
    (7, 'PRODUCTO_EXCLUSIVO_MEDELLIN', 'EXCLUSIVO'),
    (8, 'PRODUCTO_EXCLUSIVO_CALI', 'EXCLUSIVO'),
    (9, 'PRODUCTO_EXCLUSIVO_BARRANQUILLA', 'EXCLUSIVO'),
    (10, 'CREDITO_HIPOTECARIO', 'CREDITO');

INSERT INTO disponibilidad (idSucursal, idProducto) VALUES
    (1, 1), (2, 1), (3, 1),
    (1, 2), (4, 2), (5, 2),
    (2, 3), (3, 3), (4, 3),
    (1, 4), (2, 4), (5, 4),
    (3, 5), (4, 5), (5, 5),
    (1, 10), (3, 10), (1, 6),
    (2, 7), (3, 8), (4, 9);

INSERT INTO visitan (idSucursal, idCliente, fechaVisita) VALUES
    (1, 1, '2024-01-15'),
    (1, 2, '2024-01-16'),
    (2, 3, '2024-01-20'),
    (2, 4, '2024-01-22'),
    (3, 5, '2024-01-25'),
    (3, 6, '2024-01-28'),
    (4, 7, '2024-02-01'),
    (5, 8, '2024-02-05'),
    (1, 9, '2024-02-10'),
    (2, 10, '2024-02-12'),
    (2, 1, '2024-03-01'),
    (3, 4, '2024-03-05'),
    (1, 5, '2024-03-10'),
    (4, 3, '2024-03-15');

INSERT INTO inscripcion (idProducto, idCliente) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 10),
    (5, 8),
    (10, 6),
    (6, 1),
    (6, 9),
    (7, 4),
    (8, 5),
    (9, 7),
    (1, 5),
    (4, 7);
