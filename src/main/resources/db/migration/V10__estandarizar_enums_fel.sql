-- V10__estandarizar_enums_fel.sql

UPDATE ventas_fel SET fel_estado = 'PENDIENTE' WHERE fel_estado = 'Pendiente';

ALTER TABLE ventas_fel
    MODIFY COLUMN fel_estado ENUM('PENDIENTE', 'CERTIFICADA', 'ERROR', 'ANULADA') NOT NULL DEFAULT 'PENDIENTE';

ALTER TABLE ventas_fel_notas_credito
    MODIFY COLUMN nota_estado ENUM('PENDIENTE', 'CERTIFICADA', 'ERROR') NOT NULL DEFAULT 'PENDIENTE';