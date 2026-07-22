-- V15__eliminar_tipo_pago.sql

UPDATE suscripcion_pagos SET pago_plan = 'basico' WHERE pago_plan = 'pro';

ALTER TABLE suscripcion_pagos
    MODIFY COLUMN pago_plan ENUM('basico') NOT NULL;