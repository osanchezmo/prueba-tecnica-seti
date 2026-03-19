-- Parte 2 — Creación de la base de datos BTG
--
-- Con el docker-compose de ESTE repositorio NO hace falta ejecutar este archivo:
-- el servicio PostgreSQL ya crea la base `btg` (POSTGRES_DB).
--
-- Úsalo solo si tienes un PostgreSQL ajeno y aún no existe la base `btg`.
-- Conectado a la base de mantenimiento `postgres` (no a `btg`), por ejemplo:
--   docker exec -i postgres_db psql -U admin -d postgres -v ON_ERROR_STOP=1 -f - < parte-2-sql/01-create-database.sql
--   (Ruta del archivo debe ser accesible desde donde ejecutas el comando; suele ser más simple copiar el SQL a tu cliente.)

CREATE DATABASE btg
    WITH
    OWNER      = CURRENT_USER
    ENCODING   = 'UTF8'
    LC_COLLATE = 'C'
    LC_CTYPE   = 'C'
    TEMPLATE   = template0;
