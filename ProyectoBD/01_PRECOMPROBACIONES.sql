-- Solo consultas. Ejecutar en MySQL Workbench antes de la migración.
SELECT LOWER(TRIM(correoElectronico)) AS correo, COUNT(*) AS cantidad FROM USUARIO WHERE correoElectronico IS NOT NULL GROUP BY LOWER(TRIM(correoElectronico)) HAVING COUNT(*)>1;
SELECT TRIM(telefono) AS telefono,COUNT(*) AS cantidad FROM USUARIO WHERE NULLIF(TRIM(telefono),'') IS NOT NULL GROUP BY TRIM(telefono) HAVING COUNT(*)>1;
SELECT TRIM(dni) AS dni,COUNT(*) AS cantidad FROM USUARIO WHERE NULLIF(TRIM(dni),'') IS NOT NULL GROUP BY TRIM(dni) HAVING COUNT(*)>1;
SELECT idUsuario FROM USUARIO WHERE correoElectronico IS NULL OR TRIM(correoElectronico)='' OR password IS NULL;
SELECT idPedido FROM PEDIDO WHERE total<0 OR propina<0 OR costoEnvio<0;
SELECT idProducto FROM PRODUCTO WHERE precio<0;
SELECT idDetalleCarrito FROM DETALLE_CARRITO WHERE cantidad NOT BETWEEN 1 AND 99 OR precioUnitario<0;
SELECT idUsuario,idNegocio,COUNT(*) FROM CARRITO WHERE estado='ACTIVO' GROUP BY idUsuario,idNegocio HAVING COUNT(*)>1;
SELECT idDireccionUsuario FROM DIRECCION_USUARIO WHERE latitud NOT BETWEEN -90 AND 90 OR longitud NOT BETWEEN -180 AND 180;
SELECT idNegocio FROM NEGOCIO WHERE latitud NOT BETWEEN -90 AND 90 OR longitud NOT BETWEEN -180 AND 180;
SELECT idConfiguracion FROM CONFIGURACION WHERE precioBaseDelivery<0 OR precioKmAdicional<0 OR distanciaBaseMetros<0;

SELECT idConfiguracion FROM CONFIGURACION WHERE precioBaseDelivery>99999999.99 OR precioKmAdicional>99999999.99 OR ABS(precioBaseDelivery-ROUND(precioBaseDelivery,2))>0.0000001 OR ABS(precioKmAdicional-ROUND(precioKmAdicional,2))>0.0000001;
