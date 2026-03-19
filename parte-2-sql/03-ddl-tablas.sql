-- Parte 2 — Tablas y relaciones (PK / FK) en schema `public`
--
-- Ejecutar conectado a `btg` después de 02-schema.sql.

SET search_path TO public, pg_catalog;

CREATE TABLE IF NOT EXISTS cliente (
    id         INT          PRIMARY KEY NOT NULL,
    nombre     VARCHAR(255) NOT NULL,
    apellidos  VARCHAR(255) NOT NULL,
    ciudad     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS sucursal (
    id      INT          PRIMARY KEY NOT NULL,
    nombre  VARCHAR(255) NOT NULL,
    ciudad  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS producto (
    id             INT          PRIMARY KEY NOT NULL,
    nombre         VARCHAR(255) NOT NULL,
    tipoProducto   VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS inscripcion (
    idProducto INT NOT NULL,
    idCliente  INT NOT NULL,
    PRIMARY KEY (idProducto, idCliente),
    CONSTRAINT fk_inscripcion_producto FOREIGN KEY (idProducto) REFERENCES producto (id),
    CONSTRAINT fk_inscripcion_cliente  FOREIGN KEY (idCliente)  REFERENCES cliente (id)
);

CREATE TABLE IF NOT EXISTS disponibilidad (
    idSucursal INT NOT NULL,
    idProducto INT NOT NULL,
    PRIMARY KEY (idSucursal, idProducto),
    CONSTRAINT fk_disponibilidad_sucursal FOREIGN KEY (idSucursal) REFERENCES sucursal (id),
    CONSTRAINT fk_disponibilidad_producto FOREIGN KEY (idProducto) REFERENCES producto (id)
);

CREATE TABLE IF NOT EXISTS visitan (
    idSucursal  INT  NOT NULL,
    idCliente   INT  NOT NULL,
    fechaVisita DATE NOT NULL,
    PRIMARY KEY (idSucursal, idCliente),
    CONSTRAINT fk_visitan_sucursal FOREIGN KEY (idSucursal) REFERENCES sucursal (id),
    CONSTRAINT fk_visitan_cliente FOREIGN KEY (idCliente) REFERENCES cliente (id)
);
