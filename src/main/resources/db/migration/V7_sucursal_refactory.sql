-- V7__mover_nit_a_sucursal.sql

ALTER TABLE sucursales
    ADD COLUMN sucursal_nit VARCHAR(20) NULL,
    ADD CONSTRAINT uq_sucursales_nit UNIQUE (sucursal_nit),
    ADD CONSTRAINT uq_sucursales_farmacia_id UNIQUE (farmacia_id);

ALTER TABLE farmacias
DROP COLUMN farmacia_nit;