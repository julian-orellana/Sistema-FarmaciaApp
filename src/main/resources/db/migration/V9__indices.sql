-- V9__indices.sql

-- compuesto
CREATE INDEX idx_inventario_farmacia_producto ON inventario(farmacia_id, producto_id);
CREATE INDEX idx_inventario_lotes_farmacia_producto on inventario_lotes(farmacia_id, producto_id);
CREATE INDEX idx_presentacion_farmacia on presentaciones(farmacia_id, presentacion_id);
CREATE INDEX idx_venta_farmacia ON ventas(farmacia_id, sucursal_id);
CREATE INDEX idx_venta_pago_farmacia ON venta_pagos(farmacia_id, venta_id);