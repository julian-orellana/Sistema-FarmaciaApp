-- V8__crear_tabla_fel_credenciales.sql

CREATE TABLE fel_credenciales (
                                  credencial_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  sucursal_id BIGINT NOT NULL,
                                  ambiente VARCHAR(10) NOT NULL,
                                  certificador VARCHAR(30) NOT NULL DEFAULT 'INFILE',
                                  credencial_usuario_cifrado TEXT,
                                  credencial_secreto_cifrado TEXT,
                                  credencial_extra_cifrado TEXT,
                                  activa BOOLEAN NOT NULL DEFAULT FALSE,
                                  fecha_validacion DATETIME NULL,
                                  auditoria_fecha_creacion DATETIME NOT NULL,
                                  CONSTRAINT fk_fel_credenciales_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursales(sucursal_id),
                                  CONSTRAINT uq_fel_credenciales_sucursal_ambiente UNIQUE (sucursal_id, ambiente)
);