-- V20: permitir nota_numero_autorizacion NULL (se puebla solo al certificar, no al crear)
ALTER TABLE ventas_fel_notas_credito
    MODIFY nota_numero_autorizacion VARCHAR(50) NULL;
