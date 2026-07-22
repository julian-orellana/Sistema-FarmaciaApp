-- V14__eliminar_rol_usuario.sql

ALTER TABLE usuarios
    MODIFY COLUMN usuario_rol ENUM('administrador','encargado','superadmin') NOT NULL;

UPDATE farmacias SET plan = 'basico' WHERE plan = 'pro';

ALTER TABLE farmacias
    MODIFY COLUMN plan ENUM('basico') NOT NULL;