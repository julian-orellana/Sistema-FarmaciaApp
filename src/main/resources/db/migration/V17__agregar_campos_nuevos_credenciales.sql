-- V12__ajustar_fel_credenciales_tekra.sql

ALTER TABLE fel_credenciales
    MODIFY COLUMN certificador VARCHAR(30) NOT NULL,
    MODIFY COLUMN fecha_validacion DATETIME NULL,
    ADD COLUMN credencial_cliente BIGINT NOT NULL,
    ADD COLUMN credencial_contrato BIGINT NOT NULL,
    ADD COLUMN firmar_emisor BOOLEAN NOT NULL,
    ADD COLUMN validar_identificador BOOLEAN NOT NULL DEFAULT TRUE;