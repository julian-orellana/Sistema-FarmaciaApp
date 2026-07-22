

ALTER TABLE sucursales
    ADD COLUMN codigo_establecimiento VARCHAR(30) NOT NULL,
    ADD COLUMN afiliacion_iva VARCHAR(30) NOT NULL,
    ADD COLUMN municipio VARCHAR(30) NOT NULL,
    ADD COLUMN departamento VARCHAR(30) NOT NULL,
    ADD COLUMN codigo_postal VARCHAR(10) NOT NULL;