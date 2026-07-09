-- V11__eliminar_pago_plan_pro_y_chain.sql
ALTER TABLE suscripcion_pagos
    MODIFY COLUMN pago_plan ENUM('basico') NOT NULL;