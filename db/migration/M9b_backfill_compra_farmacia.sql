-- =============================================================================
-- M9b — Backfill de farmacia_id en compras y lotes creados por compra
-- =============================================================================
-- Hasta este fix, CompraServiceImpl.crear() no seteaba farmacia ni en la
-- cabecera Compra ni en los lotes nuevos que creaba: ambos quedaban con
-- farmacia_id = NULL. Las consultas tenant-scoped por farmacia_id (listar,
-- anular, buscar) no encuentran esas filas. El fix corrige de aquí en adelante;
-- este script rellena las filas históricas a partir de su sucursal.
--
-- sucursales.farmacia_id es NOT NULL en el modelo, así que es la fuente fiable.
--
-- EJECUCIÓN MANUAL. No se corre automáticamente (el proyecto no usa Flyway/Liquibase).
--
-- ANTES DE EJECUTAR:
--   1. Backup de las tablas `compras` e `inventario_lotes`.
--   2. Ejecutar el SELECT de diagnóstico y revisar el conteo esperado.
-- =============================================================================

-- 1) Diagnóstico: cuántas filas tienen farmacia_id NULL y si su sucursal la resuelve.
SELECT 'compras'         AS tabla,
       COUNT(*)          AS con_farmacia_null,
       SUM(CASE WHEN s.farmacia_id IS NULL THEN 1 ELSE 0 END) AS sin_sucursal_resoluble
FROM        compras c
LEFT JOIN   sucursales s ON s.sucursal_id = c.sucursal_id
WHERE       c.farmacia_id IS NULL
UNION ALL
SELECT 'inventario_lotes',
       COUNT(*),
       SUM(CASE WHEN s.farmacia_id IS NULL THEN 1 ELSE 0 END)
FROM        inventario_lotes l
LEFT JOIN   sucursales s ON s.sucursal_id = l.sucursal_id
WHERE       l.farmacia_id IS NULL;

-- 2) Backfill. Solo toca filas con farmacia_id NULL; recupera la farmacia de la sucursal.
START TRANSACTION;

UPDATE compras c
JOIN   sucursales s ON s.sucursal_id = c.sucursal_id
SET    c.farmacia_id = s.farmacia_id
WHERE  c.farmacia_id IS NULL;

UPDATE inventario_lotes l
JOIN   sucursales s ON s.sucursal_id = l.sucursal_id
SET    l.farmacia_id = s.farmacia_id
WHERE  l.farmacia_id IS NULL;

-- Revisar las filas afectadas y, si es correcto:
COMMIT;
-- En caso de duda: ROLLBACK;

-- RECOMENDACIÓN (cambio de esquema aparte, solo seguro DESPUÉS de este backfill):
-- endurecer las columnas a NOT NULL para que el descuido no pueda repetirse:
--   ALTER TABLE compras          MODIFY farmacia_id BIGINT NOT NULL;
--   ALTER TABLE inventario_lotes MODIFY farmacia_id BIGINT NOT NULL;
