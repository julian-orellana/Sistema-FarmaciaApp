UPDATE venta_pagos SET metodo_de_pago = 'EFECTIVO' WHERE metodo_de_pago = 'efectivo';
UPDATE venta_pagos SET metodo_de_pago = 'TARJETA_CREDITO' WHERE metodo_de_pago = 'tarjetaDeCredito';
UPDATE venta_pagos SET metodo_de_pago = 'TARJETA_DEBITO' WHERE metodo_de_pago = 'tarjetaDeDebito';