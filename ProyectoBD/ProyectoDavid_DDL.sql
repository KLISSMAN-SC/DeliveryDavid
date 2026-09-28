-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: proyecto_david
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categorias_producto`
--

DROP TABLE IF EXISTS `categorias_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias_producto` (
  `idCategoriaProducto` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `idNegocio` int NOT NULL,
  PRIMARY KEY (`idCategoriaProducto`),
  UNIQUE KEY `id_categoria_producto_UNIQUE` (`idCategoriaProducto`),
  KEY `fk_CATEGORIAS_PRODUCTO_NEGOCIO1_idx` (`idNegocio`),
  CONSTRAINT `fk_CATEGORIAS_PRODUCTO_NEGOCIO1` FOREIGN KEY (`idNegocio`) REFERENCES `negocio` (`idNegocio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias_producto`
--

LOCK TABLES `categorias_producto` WRITE;
/*!40000 ALTER TABLE `categorias_producto` DISABLE KEYS */;
/*!40000 ALTER TABLE `categorias_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `centro_comercial`
--

DROP TABLE IF EXISTS `centro_comercial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `centro_comercial` (
  `idCentroComercial` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) NOT NULL,
  `direccion` varchar(45) DEFAULT NULL,
  `latitud` decimal(10,8) DEFAULT NULL,
  `longitud` decimal(10,8) DEFAULT NULL,
  PRIMARY KEY (`idCentroComercial`),
  UNIQUE KEY `id_centro_comercial_UNIQUE` (`idCentroComercial`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `centro_comercial`
--

LOCK TABLES `centro_comercial` WRITE;
/*!40000 ALTER TABLE `centro_comercial` DISABLE KEYS */;
INSERT INTO `centro_comercial` VALUES (1,'Real Plaza Pucallpa','Av. Centenario km 5',-8.38550000,-74.55440000),(2,'Open Plaza Pucallpa','Av. Centenario 2086, Yarinacocha',-8.38720000,-74.56880000);
/*!40000 ALTER TABLE `centro_comercial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_pedido`
--

DROP TABLE IF EXISTS `detalle_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_pedido` (
  `idDetallePedido` int NOT NULL AUTO_INCREMENT,
  `cantidad` int DEFAULT NULL,
  `precioUnitario` decimal(10,2) DEFAULT NULL,
  `subtotal` decimal(10,2) DEFAULT NULL,
  `notaEspecial` varchar(100) DEFAULT NULL,
  `idPedido` int NOT NULL,
  `idProducto` int NOT NULL,
  PRIMARY KEY (`idDetallePedido`),
  KEY `fk_DETALLE_PEDIDO_PEDIDO1_idx` (`idPedido`),
  KEY `fk_DETALLE_PEDIDO_PRODUCTO1_idx` (`idProducto`),
  CONSTRAINT `fk_DETALLE_PEDIDO_PEDIDO1` FOREIGN KEY (`idPedido`) REFERENCES `pedido` (`idPedido`),
  CONSTRAINT `fk_DETALLE_PEDIDO_PRODUCTO1` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_pedido`
--

LOCK TABLES `detalle_pedido` WRITE;
/*!40000 ALTER TABLE `detalle_pedido` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalle_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `negocio`
--

DROP TABLE IF EXISTS `negocio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `negocio` (
  `idNegocio` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `direccion` varchar(50) DEFAULT NULL,
  `latitud` decimal(10,8) DEFAULT NULL,
  `longitud` decimal(10,8) DEFAULT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `estado` varchar(10) DEFAULT NULL,
  `imagenLogo` varchar(200) DEFAULT NULL,
  `idTipoNegocio` int NOT NULL,
  `idZona` int NOT NULL,
  `idCentroComercial` int DEFAULT NULL,
  PRIMARY KEY (`idNegocio`),
  UNIQUE KEY `id_negocio_UNIQUE` (`idNegocio`),
  KEY `fk_NEGOCIOS_TIPO_NEGOCIO_idx` (`idTipoNegocio`),
  KEY `fk_NEGOCIO_ZONA1_idx` (`idZona`),
  KEY `fk_NEGOCIO_CENTRO_COMERCIAL1_idx` (`idCentroComercial`),
  CONSTRAINT `fk_NEGOCIO_CENTRO_COMERCIAL1` FOREIGN KEY (`idCentroComercial`) REFERENCES `centro_comercial` (`idCentroComercial`),
  CONSTRAINT `fk_NEGOCIO_ZONA1` FOREIGN KEY (`idZona`) REFERENCES `zona` (`idZona`),
  CONSTRAINT `fk_NEGOCIOS_TIPO_NEGOCIO` FOREIGN KEY (`idTipoNegocio`) REFERENCES `tipo_negocio` (`idTipoNegocio`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `negocio`
--

LOCK TABLES `negocio` WRITE;
/*!40000 ALTER TABLE `negocio` DISABLE KEYS */;
INSERT INTO `negocio` VALUES (28,'Tierra Verde','Av. San Martín Mz A',-8.38310000,-74.55210000,'999111222','ABIERTO','tierra_verde.jpg',1,1,NULL),(37,'Manu Inmaculada','Jr. Inmaculada 456',-8.38420000,-74.55320000,'999222333','ABIERTO','manu_inmaculada.jpg',1,1,NULL),(38,'Typical Wings','Av. Yarinacocha 789',-8.36540000,-74.57120000,'999333444','ABIERTO','typical_wings.jpg',1,1,NULL),(39,'Manu Cafe Real Plaza','Av. Centenario (Real Plaza)',-8.38550000,-74.55440000,'999444555','ABIERTO','manu_real_plaza.jpg',1,1,1),(40,'Life Jugos Y Snack','Jr. Tarapacá 111',-8.38120000,-74.55110000,'999555666','ABIERTO','life_jugos.jpg',1,1,NULL),(41,'Donatito','Jr. Sucre 222',-8.38230000,-74.55220000,'999666777','ABIERTO','donatito.jpg',1,1,NULL),(42,'Dolce Candy\'s','Jr. 7 de Junio 333',-8.38000000,-74.55000000,'999777888','ABIERTO','dolce_candys.jpg',1,1,NULL),(43,'Tropical Fruit','Jr. Ucayali 444',-8.37910000,-74.54920000,'999888999','ABIERTO','tropical_fruit.jpg',1,1,NULL),(44,'Esencia Bakery','Jr. Guillermo Sisley 555',-8.38440000,-74.56110000,'999000111','ABIERTO','esencia_bakery.jpg',1,1,NULL);
/*!40000 ALTER TABLE `negocio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido` (
  `idPedido` int NOT NULL AUTO_INCREMENT,
  `estadoPedido` varchar(45) DEFAULT NULL,
  `metodoPago` enum('Efectivo','Yape','Plin') DEFAULT NULL,
  `costoEnvio` decimal(10,2) DEFAULT NULL,
  `propina` decimal(10,2) DEFAULT NULL,
  `total` decimal(10,2) DEFAULT NULL,
  `direccionEnvio` varchar(100) DEFAULT NULL,
  `referencia` varchar(100) DEFAULT NULL,
  `latitudEnvio` decimal(10,8) DEFAULT NULL,
  `longitudEnvio` decimal(10,8) DEFAULT NULL,
  `fechaHora` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `idUsuario` int NOT NULL,
  `idNegocio` int NOT NULL,
  `idRepartidor` int DEFAULT NULL,
  `costo_envio` decimal(10,2) DEFAULT NULL,
  `estado_pedido` varchar(45) DEFAULT NULL,
  `fecha_hora` datetime(6) DEFAULT NULL,
  `latitud_envio` decimal(10,8) DEFAULT NULL,
  `longitud_envio` decimal(10,8) DEFAULT NULL,
  `metodo_pago` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idPedido`),
  UNIQUE KEY `idPedido_UNIQUE` (`idPedido`),
  KEY `fk_PEDIDO_USUARIO1_idx` (`idUsuario`),
  KEY `fk_PEDIDO_NEGOCIO1_idx` (`idNegocio`),
  KEY `fk_PEDIDO_USUARIO2_idx` (`idRepartidor`),
  CONSTRAINT `fk_PEDIDO_NEGOCIO1` FOREIGN KEY (`idNegocio`) REFERENCES `negocio` (`idNegocio`),
  CONSTRAINT `fk_PEDIDO_USUARIO1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`),
  CONSTRAINT `fk_PEDIDO_USUARIO2` FOREIGN KEY (`idRepartidor`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `idProducto` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `precio` decimal(10,2) DEFAULT NULL,
  `imagen` varchar(255) DEFAULT NULL,
  `disponible` tinyint(1) DEFAULT '1',
  `idNegocio` int NOT NULL,
  `idCategoriaProducto` int NOT NULL,
  PRIMARY KEY (`idProducto`),
  KEY `fk_PRODUCTO_NEGOCIO1_idx` (`idNegocio`),
  KEY `fk_PRODUCTO_CATEGORIAS_PRODUCTO1_idx` (`idCategoriaProducto`),
  CONSTRAINT `fk_PRODUCTO_CATEGORIAS_PRODUCTO1` FOREIGN KEY (`idCategoriaProducto`) REFERENCES `categorias_producto` (`idCategoriaProducto`),
  CONSTRAINT `fk_PRODUCTO_NEGOCIO1` FOREIGN KEY (`idNegocio`) REFERENCES `negocio` (`idNegocio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `idRol` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idRol`),
  UNIQUE KEY `idRol_UNIQUE` (`idRol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_negocio`
--

DROP TABLE IF EXISTS `tipo_negocio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_negocio` (
  `idTipoNegocio` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `icono` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`idTipoNegocio`),
  UNIQUE KEY `id_tipo_negocio_UNIQUE` (`idTipoNegocio`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_negocio`
--

LOCK TABLES `tipo_negocio` WRITE;
/*!40000 ALTER TABLE `tipo_negocio` DISABLE KEYS */;
INSERT INTO `tipo_negocio` VALUES (1,'Restaurantes','restaurantes.png'),(2,'Súper','super.png'),(3,'Farmacias','farmacias.png'),(4,'Tiendas','tiendas.png'),(5,'Bebidas','bebidas.png'),(6,'Mascotas','mascotas.png');
/*!40000 ALTER TABLE `tipo_negocio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `idUsuario` int NOT NULL AUTO_INCREMENT,
  `idRol` int NOT NULL,
  `nombres` varchar(45) DEFAULT NULL,
  `apellidos` varchar(45) DEFAULT NULL,
  `dni` varchar(8) DEFAULT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `correoElectronico` varchar(45) DEFAULT NULL,
  `password` varchar(200) DEFAULT NULL,
  `fechaCreacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`idUsuario`),
  KEY `fk_USUARIO_ROL1_idx` (`idRol`),
  CONSTRAINT `fk_USUARIO_ROL1` FOREIGN KEY (`idRol`) REFERENCES `rol` (`idRol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zona`
--

DROP TABLE IF EXISTS `zona`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `zona` (
  `idZona` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `costoEnvioBase` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`idZona`),
  UNIQUE KEY `idZona_UNIQUE` (`idZona`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zona`
--

LOCK TABLES `zona` WRITE;
/*!40000 ALTER TABLE `zona` DISABLE KEYS */;
INSERT INTO `zona` VALUES (1,'SAPORI',3.00),(2,'SELVA PLAZA',3.00),(3,'REAL PLAZA',3.00),(4,'CENTRO',3.00),(5,'CALLERIA',4.00),(6,'YARINA',5.00),(7,'MANANTAY',5.00);
/*!40000 ALTER TABLE `zona` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-28 15:43:11
