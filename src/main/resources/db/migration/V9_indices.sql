CREATE INDEX idx_producto_nombre ON productos(producto_nombre);
CREATE INDEX idx_producto_barras on productos(producto_codigo_barras);
CREATE INDEX idx_alertarTipo on alertas(alerta_tipo);
CREATE INDEX idx_lote_vencimiento on inventario_lotes(lote_fecha_vencimiento);
CREATE INDEX idx_inventario_farmacia ON inventario(farmacia_id);
CREATE INDEX idx_inventario_cantidad ON inventario(inventario_cantidad_actual, inventario_cantidad_minima);
CREATE INDEX idx_presentacion_nombre on presentacion(presentacion_nombre);
CREATE INDEX idx_venta_cliente on ventas(venta_nombre_cliente);



--compuesto
CREATE INDEX idx_inventario_farmacia_producto ON inventario(farmacia_id, producto_id);
CREATE INDEX idx_inventario_lotes_farmacia_producto on inventario_lotes(farmacia_id, producto_id);
CREATE INDEX idx_presentacion_farmacia on presentacion(farmacia_id, presentacion_id);
CREATE INDEX idx_venta_farmacia ON ventas(farmacia_id, sucursal_id);
CREATE INDEX idx_venta_pago_farmacia ON venta_pagos(farmacia_id, venta_id);