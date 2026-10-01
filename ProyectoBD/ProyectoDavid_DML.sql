INSERT INTO `centro_comercial` VALUES 
(1,'Real Plaza Pucallpa','Av. Centenario km 5',-8.38550000,-74.55440000),
(2,'Open Plaza Pucallpa','Av. Centenario 2086, Yarinacocha',-8.38720000,-74.56880000);

INSERT INTO `tipo_negocio` VALUES 
(1,'Restaurantes','restaurantes.png'),
(2,'Súper','super.png'),
(3,'Farmacias','farmacias.png'),
(4,'Tiendas','tiendas.png'),
(5,'Bebidas','bebidas.png'),
(6,'Mascotas','mascotas.png');

INSERT INTO `zona` VALUES 
(1,'SAPORI',3.00),
(2,'SELVA PLAZA',3.00),
(3,'REAL PLAZA',3.00),
(4,'CENTRO',3.00),
(5,'CALLERIA',4.00),
(6,'YARINA',5.00),
(7,'MANANTAY',5.00);

INSERT INTO `negocio` VALUES 
(28,'Tierra Verde','Av. San Martín Mz A',-8.38310000,-74.55210000,'999111222','ABIERTO','tierra_verde.jpg',1,1,NULL),
(37,'Manu Inmaculada','Jr. Inmaculada 456',-8.38420000,-74.55320000,'999222333','ABIERTO','manu_inmaculada.jpg',1,1,NULL),
(38,'Typical Wings','Av. Yarinacocha 789',-8.36540000,-74.57120000,'999333444','ABIERTO','typical_wings.jpg',1,1,NULL),
(39,'Manu Cafe Real Plaza','Av. Centenario (Real Plaza)',-8.38550000,-74.55440000,'999444555','ABIERTO','manu_real_plaza.jpg',1,1,1),
(40,'Norkys Polleria','Jr. Tarapacá 111',-8.38120000,-74.55110000,'999555666','ABIERTO','Norky_polleria.jpg',1,1,NULL),
(41,'Donatito','Jr. Sucre 222',-8.38230000,-74.55220000,'999666777','ABIERTO','donatito.jpg',1,1,NULL),
(42,'Dolce Candy\'s','Jr. 7 de Junio 333',-8.38000000,-74.55000000,'999777888','ABIERTO','dolce_candys.jpg',1,1,NULL),
(43,'Tropical Fruit','Jr. Ucayali 444',-8.37910000,-74.54920000,'999888999','ABIERTO','tropical_fruit.jpg',1,1,NULL),
(44,'Esencia Bakery','Jr. Guillermo Sisley 555',-8.38440000,-74.56110000,'999000111','ABIERTO','esencia_bakery.jpg',1,1,NULL);

INSERT INTO `rol` (`idRol`, `nombre`) VALUES 
(1, 'Administrador'),
(2, 'Cliente'),
(3, 'Repartidor');

INSERT INTO `usuario` (`idUsuario`, `idRol`, `nombres`, `apellidos`, `dni`, `telefono`, `correoElectronico`, `password`, `fecha_creacion`) VALUES 
(1, 2, 'Carlos Andre', 'Ramirez Cachique', '71234567', '987654321', 'carlos@cliente.com', 'hash_pass_123', NOW()),
(2, 3, 'Miguel', 'Gómez', '45678912', '999888777', 'miguel@delivery.com', 'hash_pass_456', NOW()),
(3, 1, 'Admin', 'Sistema', '00000000', '900000000', 'admin@sistema.com', 'admin_pass', NOW());

INSERT INTO categorias_producto (idCategoriaProducto, nombre,logo, idNegocio) VALUES 
(1, 'Alitas','alitas.jpg' ,38),
(2, 'Desayunos','desayunos.jpg' ,38),
(3, 'Hamburguesas', 'hamburguesas.jpg',39),
(4, 'Polleria', 'polleria.jpg',40),
(5, 'Licores','licores.jpg', 44),
(6, 'Dulces', 'dulces.jpg',39),
(7, 'Pizza', 'pizza.jpg',41),
(8, 'Marino', 'marino.jpg',28),
(9, 'Makis', 'makis.jpg',28);

INSERT INTO `producto` (`idProducto`, `nombre`, `descripcion`, `precio`, `imagen`, `disponible`, `idNegocio`, `idCategoriaProducto`) VALUES 
(1, 'Alitas BBQ (6 und)', 'Bañadas en salsa BBQ dulce con porción de papas', 16.00, 'alitas_bbq.jpg', 1, 38, 1),
(2, 'Alitas Acevichadas (6 und)', 'Con salsa acevichada y toques de limón', 18.00, 'alitas_acevichadas.jpg', 1, 38, 1),
(3, 'Chicha Morada 1L', 'Refrescante chicha helada', 7.00, 'chicha_1l.jpg', 1, 38, 2),
(4, 'Frappé de Chocolate', 'Bebida dulce helada con crema batida y fudge', 14.50, 'frappe_choco.jpg', 1, 39, 3),
(5, 'Porción Torta de Chocolate', 'Bizcocho húmedo relleno de fudge', 10.00, 'torta_choco.jpg', 1, 39, 4);

INSERT INTO `pedido` (`idPedido`, `estadoPedido`, `metodoPago`, `costoEnvio`, `propina`, `total`, `direccionEnvio`, `referencia`, `latitudEnvio`, `longitudEnvio`, `fechaHora`, `idUsuario`, `idNegocio`, `idRepartidor`) VALUES 
(1, 'CONFIRMADO', 'Efectivo', 3.00, 0.00, 17.50, 'Av. Miraflores Mz B', 'Casa con portón blanco', -8.38400000, -74.55100000, NOW(), 1, 39, 2),
(2, 'PENDIENTE_DE_WHATSAPP', 'Yape', 3.00, 0.00, 46.00, 'Av. Miraflores Mz B', 'Casa con portón blanco', -8.38400000, -74.55100000, NOW(), 1, 38, NULL);

INSERT INTO `detalle_pedido` (`idDetallePedido`, `cantidad`, `precioUnitario`, `subtotal`, `notaEspecial`, `idPedido`, `idProducto`) VALUES 
(1, 1, 14.50, 14.50, 'Con extra crema batida por favor', 1, 4),
(2, 2, 18.00, 36.00, 'Las papas bien fritas', 2, 2),
(3, 1, 7.00, 7.00, 'Helada', 2, 3);

INSERT INTO `carrito` (`idCarrito`, `fecha_actualizacion`, `idUsuario`, `idNegocio`) VALUES 
(1, NOW(), 1, 39);

INSERT INTO `detalle_carrito` (`idDetalleCarrito`, `cantidad`, `notaEspecial`, `idCarrito`, `idProducto`) VALUES 
(1, 2, 'Uno de ellos sin crema batida por favor', 1, 4),
(2, 1, NULL, 1, 5);

INSERT INTO `promocion` (`idPromocion`, `titulo`, `descripcion`, `imagenBanner`, `fecha_inicio`, `fecha_fin`, `estado`, `idNegocio`, `idProducto`) VALUES 
(1, '¡Locura de Alitas BBQ!', 'Disfruta nuestras Alitas BBQ con porción doble de papas fritas solo por este fin de semana.', 'banner_promo_alitas.jpg', '2026-09-01 08:00:00', '2026-10-31 23:59:59', 1, 38, 1),
(2, 'Tardes de Frappé en el Real Plaza', 'Alivia el calor de Pucallpa con nuestro increíble Frappé de Chocolate.', 'banner_promo_frappe.jpg', '2026-09-15 08:00:00', '2026-10-15 23:59:59', 1, 39, 4),
(3, '¡Nuevos Postres en Manu Cafe!', 'Ven a probar nuestra nueva carta de dulces y tortas. ¡Te esperamos!', 'banner_nuevos_postres.jpg', '2026-09-20 08:00:00', '2026-11-30 23:59:59', 1, 39, NULL);

