DROP TABLE kardex;

CREATE TABLE kardex (
                        kardex_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                        fecha_movimiento DATETIME     NOT NULL,
                        tipo_movimiento  VARCHAR(20)  NOT NULL,
                        cantidad         INT          NOT NULL,
                        stock_anterior   INT          NOT NULL,
                        stock_resultante INT          NOT NULL,
                        costo_unitario   DECIMAL(10,2),
                        referencia_id    BIGINT,
                        referencia_tipo  VARCHAR(20),
                        observacion      VARCHAR(255),
                        producto_id      BIGINT       NOT NULL,
                        farmacia_id      BIGINT       NOT NULL,
                        usuario_id       BIGINT       NOT NULL,
                        CONSTRAINT fk_kardex_producto FOREIGN KEY (producto_id)  REFERENCES producto(producto_id),
                        CONSTRAINT fk_kardex_farmacia FOREIGN KEY (farmacia_id)  REFERENCES farmacia(farmacia_id),
                        CONSTRAINT fk_kardex_usuario  FOREIGN KEY (usuario_id)   REFERENCES usuario(usuario_id)
);