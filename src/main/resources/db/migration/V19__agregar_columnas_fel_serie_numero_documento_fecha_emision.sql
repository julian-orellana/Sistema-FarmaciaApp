
   -- Nuevas columnas (Complemento para funcionamiento de Tekra)

ALTER TABLE ventas_fel
    ADD COLUMN fel_serie VARCHAR(50)  NULL,
    ADD COLUMN fel_numero_documento VARCHAR(50)  NULL,
    ADD COLUMN fel_fecha_emision DATETIME NULL;