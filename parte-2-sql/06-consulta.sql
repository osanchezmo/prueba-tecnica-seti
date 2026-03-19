-- Parte 2 — Consulta principal
--
-- Obtener los nombres de los clientes que tienen inscrito algún producto
-- disponible SOLO en las sucursales que visitan.
--
-- Interpretación: un cliente califica si existe al menos un producto en el que
-- está inscrito tal que TODAS las sucursales donde ese producto está disponible
-- son sucursales que ese cliente visita (no hay ninguna sucursal donde el
-- producto esté disponible y el cliente no haya visitado).
--
-- Ejecutar conectado a la base `btg`, schema `public`.

SET search_path TO public, pg_catalog;

SELECT DISTINCT c.nombre
FROM cliente c
JOIN inscripcion i ON c.id = i.idCliente
WHERE NOT EXISTS (
    -- No debe existir ninguna sucursal donde el producto esté disponible
    -- que el cliente NO haya visitado
    SELECT 1
    FROM disponibilidad d
    WHERE d.idProducto = i.idProducto
      AND NOT EXISTS (
          SELECT 1
          FROM visitan v
          WHERE v.idSucursal = d.idSucursal
            AND v.idCliente = c.id
      )
);

-- Resultado esperado con los datos de 05-datos-semilla.sql:
--   Juan Carlos    (cliente 1 — producto 6 disponible solo en sucursal 1, que visita)
--   Ana Isabel     (cliente 4 — producto 7 disponible solo en sucursal 2, que visita)
--   Luis Fernando  (cliente 5 — producto 8 disponible solo en sucursal 3, que visita)
--   Roberto        (cliente 7 — producto 9 disponible solo en sucursal 4, que visita)
--   Diego          (cliente 9 — producto 6 disponible solo en sucursal 1, que visita)
