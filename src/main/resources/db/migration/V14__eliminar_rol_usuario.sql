-- V9__eliminar_rol_vendedor.sql
ALTER TABLE usuarios
    MODIFY COLUMN usuario_rol ENUM('administrador','encargado','superadmin') NOT NULL;


-- V10__eliminar_planes_pro_y_chain.sql
    ALTER TABLE farmacias
        MODIFY COLUMN plan ENUM('basico') NOT NULL;