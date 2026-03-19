-- Parte 2 — Constantes de dominio (restricciones que fijan valores permitidos)
--
-- Ejecutar conectado a `btg` después de 03-ddl-tablas.sql y antes de cargar datos.

SET search_path TO public, pg_catalog;

ALTER TABLE producto
    DROP CONSTRAINT IF EXISTS chk_producto_tipo_producto;

ALTER TABLE producto
    ADD CONSTRAINT chk_producto_tipo_producto
    CHECK (tipoProducto IN ('FPV', 'FIC', 'EXCLUSIVO', 'CREDITO'));

COMMENT ON CONSTRAINT chk_producto_tipo_producto ON producto IS
    'Dominio fijo de tipoProducto (constantes de catálogo).';

COMMENT ON TABLE cliente IS 'Clientes BTG.';
COMMENT ON TABLE sucursal IS 'Sucursales.';
COMMENT ON TABLE producto IS 'Productos de inversión y crédito.';
COMMENT ON TABLE inscripcion IS 'Inscripción de clientes a productos.';
COMMENT ON TABLE disponibilidad IS 'Productos ofrecidos por sucursal.';
COMMENT ON TABLE visitan IS 'Relación cliente–sucursal visitada (y fecha).';
