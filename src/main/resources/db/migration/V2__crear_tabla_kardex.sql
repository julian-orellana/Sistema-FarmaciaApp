CREATE TABLE kardex (
                        id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                        farmacia_id     BIGINT NOT NULL,
                        producto_id     BIGINT NOT NULL,
                        fecha_ingreso   DATETIME NOT NULL,
                        precio_compra   DECIMAL(10,2),
                        precio_venta    DECIMAL(10,2),
                        cantidad        DECIMAL(10,2) NOT NULL,
                        ventas          DECIMAL(10,2),
                        stock           DECIMAL(10,2) NOT NULL,
                        tipo_movimiento ENUM('ENTRADA', 'SALIDA', 'AJUSTE') NOT NULL,
                        usuario_id      BIGINT,
                        created_at      DATETIME DEFAULT NOW()
);