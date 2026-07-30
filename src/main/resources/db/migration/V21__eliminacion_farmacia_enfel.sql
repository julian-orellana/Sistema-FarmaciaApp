-- Eliminación del campo farmacia en la tabla VentaFel

ALTER TABLE ventas_fel DROP FOREIGN KEY FKh47mxwli782l4dq6gyaftmrxv;
ALTER TABLE ventas_fel DROP COLUMN farmacia_id;
ALTER TABLE ventas_fel MODIFY COLUMN sucursal_id BIGINT NOT NULL;