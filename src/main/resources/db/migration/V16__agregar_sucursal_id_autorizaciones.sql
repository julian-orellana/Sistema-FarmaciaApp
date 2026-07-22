-- V16__agregar_sucursal_id_autorizaciones.sql
-- Agrega sucursal_id NOT NULL a autorizaciones para homologar el tenancy por sucursal.
-- Dev sin datos reales: se trunca la tabla para poder agregar la columna NOT NULL.

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE autorizaciones;
SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE autorizaciones
    ADD COLUMN sucursal_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_autorizaciones_sucursal
        FOREIGN KEY (sucursal_id) REFERENCES sucursales(sucursal_id);
