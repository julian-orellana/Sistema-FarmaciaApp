-- V12__sucursal_refactory.sql

ALTER TABLE sucursales
    ADD CONSTRAINT uq_sucursales_farmacia_id UNIQUE (farmacia_id);