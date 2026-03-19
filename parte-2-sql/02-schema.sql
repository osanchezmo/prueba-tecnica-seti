-- Parte 2 — Schema por defecto `public` (base de datos `btg`)
--
-- Las tablas viven en `public` para que aparezcan en el explorador del cliente
-- (VS Code, DBeaver, DataGrip, etc.) sin cambiar filtros de schema.
--
-- Si ejecutaste una versión anterior que creaba el schema PostgreSQL `btg`, se elimina aquí.
-- Ejecutar ya conectado a la base `btg`.

DROP SCHEMA IF EXISTS btg CASCADE;

SET search_path TO public, pg_catalog;

COMMENT ON SCHEMA public IS 'Parte 2: tablas cliente, sucursal, producto, inscripcion, disponibilidad, visitan.';
