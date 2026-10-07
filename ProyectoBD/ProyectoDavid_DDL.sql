-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema proyecto_david
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `proyecto_david` DEFAULT CHARACTER SET utf8 ;
USE `proyecto_david` ;

-- -----------------------------------------------------
-- Table `proyecto_david`.`ROL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`ROL` (
  `idRol` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  PRIMARY KEY (`idRol`),
  UNIQUE INDEX `idRol_UNIQUE` (`idRol` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyecto_david`.`USUARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`USUARIO` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `idRol` INT NOT NULL,
  `nombres` VARCHAR(45) NULL,
  `apellidos` VARCHAR(45) NULL,
  `dni` VARCHAR(8) NULL,
  `telefono` VARCHAR(15) NULL,
  `correoElectronico` VARCHAR(45) NULL,
  `password` VARCHAR(200) NULL,
  `fechaCreacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idUsuario`),
  INDEX `fk_USUARIO_ROL1_idx` (`idRol` ASC) VISIBLE,
  CONSTRAINT `fk_USUARIO_ROL1`
    FOREIGN KEY (`idRol`)
    REFERENCES `proyecto_david`.`ROL` (`idRol`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyecto_david`.`TIPO_NEGOCIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`TIPO_NEGOCIO` (
  `idTipoNegocio` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `icono` VARCHAR(200) NULL,
  PRIMARY KEY (`idTipoNegocio`),
  UNIQUE INDEX `id_tipo_negocio_UNIQUE` (`idTipoNegocio` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyecto_david`.`ZONA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`ZONA` (
  `idZona` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `costoEnvioBase` DECIMAL(10,2) NULL,
  PRIMARY KEY (`idZona`),
  UNIQUE INDEX `idZona_UNIQUE` (`idZona` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyecto_david`.`CENTRO_COMERCIAL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`CENTRO_COMERCIAL` (
  `idCentroComercial` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `direccion` VARCHAR(45) NULL,
  `latitud` DECIMAL(10,8) NULL,
  `longitud` DECIMAL(10,8) NULL,
  PRIMARY KEY (`idCentroComercial`),
  UNIQUE INDEX `id_centro_comercial_UNIQUE` (`idCentroComercial` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyecto_david`.`NEGOCIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`NEGOCIO` (
  `idNegocio` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `direccion` VARCHAR(50) NULL,
  `latitud` DECIMAL(10,8) NULL,
  `longitud` DECIMAL(10,8) NULL,
  `telefono` VARCHAR(15) NULL,
  `estado` ENUM('ABIERTO', 'CERRADO') NULL DEFAULT 'ABIERTO',
  `imagenLogo` VARCHAR(200) NULL,
  `idTipoNegocio` INT NOT NULL,
  `idZona` INT NOT NULL,
  `idCentroComercial` INT NULL,
  `horaInicio` TIME NULL,
  `horaFin` TIME NULL,
  PRIMARY KEY (`idNegocio`),
  UNIQUE INDEX `id_negocio_UNIQUE` (`idNegocio` ASC) VISIBLE,
  INDEX `fk_NEGOCIOS_TIPO_NEGOCIO_idx` (`idTipoNegocio` ASC) VISIBLE,
  INDEX `fk_NEGOCIO_ZONA1_idx` (`idZona` ASC) VISIBLE,
  INDEX `fk_NEGOCIO_CENTRO_COMERCIAL1_idx` (`idCentroComercial` ASC) VISIBLE,
  CONSTRAINT `fk_NEGOCIOS_TIPO_NEGOCIO`
    FOREIGN KEY (`idTipoNegocio`)
    REFERENCES `proyecto_david`.`TIPO_NEGOCIO` (`idTipoNegocio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NEGOCIO_ZONA1`
    FOREIGN KEY (`idZona`)
    REFERENCES `proyecto_david`.`ZONA` (`idZona`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NEGOCIO_CENTRO_COMERCIAL1`
    FOREIGN KEY (`idCentroComercial`)
    REFERENCES `proyecto_david`.`CENTRO_COMERCIAL` (`idCentroComercial`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyecto_david`.`CATEGORIAS_PRODUCTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`CATEGORIAS_PRODUCTO` (
  `idCategoriaProducto` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `logo`   VARCHAR(45) NULL,
  `idNegocio` INT NOT NULL,
  PRIMARY KEY (`idCategoriaProducto`),
  UNIQUE INDEX `id_categoria_producto_UNIQUE` (`idCategoriaProducto` ASC) VISIBLE,
  INDEX `fk_CATEGORIAS_PRODUCTO_NEGOCIO1_idx` (`idNegocio` ASC) VISIBLE,
  CONSTRAINT `fk_CATEGORIAS_PRODUCTO_NEGOCIO1`
    FOREIGN KEY (`idNegocio`)
    REFERENCES `proyecto_david`.`NEGOCIO` (`idNegocio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyecto_david`.`PRODUCTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`PRODUCTO` (
  `idProducto` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `descripcion` VARCHAR(255) NULL,
  `precio` DECIMAL(10,2) NULL,
  `imagen` VARCHAR(255) NULL,
  `disponible` TINYINT(1) NULL DEFAULT 1,
  `idNegocio` INT NOT NULL,
  `idCategoriaProducto` INT NOT NULL,
  PRIMARY KEY (`idProducto`),
  INDEX `fk_PRODUCTO_NEGOCIO1_idx` (`idNegocio` ASC) VISIBLE,
  INDEX `fk_PRODUCTO_CATEGORIAS_PRODUCTO1_idx` (`idCategoriaProducto` ASC) VISIBLE,
  CONSTRAINT `fk_PRODUCTO_NEGOCIO1`
    FOREIGN KEY (`idNegocio`)
    REFERENCES `proyecto_david`.`NEGOCIO` (`idNegocio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PRODUCTO_CATEGORIAS_PRODUCTO1`
    FOREIGN KEY (`idCategoriaProducto`)
    REFERENCES `proyecto_david`.`CATEGORIAS_PRODUCTO` (`idCategoriaProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyecto_david`.`PEDIDO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`PEDIDO` (
  `idPedido` INT NOT NULL AUTO_INCREMENT,
  `estadoPedido` ENUM('PENDIENTE_DE_WHATSAPP','CONFIRMADO','EN_CAMINO','ENTREGADO','CANCELADO') NULL DEFAULT 'PENDIENTE_DE_WHATSAPP',
  `metodoPago` VARCHAR(45) NULL,
  `costoEnvio` DECIMAL(10,2) NULL,
  `propina` DECIMAL(10,2) NULL,
  `total` DECIMAL(10,2) NULL,
  `direccionEnvio` VARCHAR(100) NULL,
  `referencia` VARCHAR(100) NULL,
  `latitudEnvio` DECIMAL(10,8) NULL,
  `longitudEnvio` DECIMAL(10,8) NULL,
  `fechaHora` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `idUsuario` INT NOT NULL,
  `idNegocio` INT NOT NULL,
  `idRepartidor` INT NULL,
  PRIMARY KEY (`idPedido`),
  UNIQUE INDEX `idPedido_UNIQUE` (`idPedido` ASC) VISIBLE,
  INDEX `fk_PEDIDO_USUARIO1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_PEDIDO_NEGOCIO1_idx` (`idNegocio` ASC) VISIBLE,
  INDEX `fk_PEDIDO_USUARIO2_idx` (`idRepartidor` ASC) VISIBLE,
  CONSTRAINT `fk_PEDIDO_USUARIO1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `proyecto_david`.`USUARIO` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PEDIDO_NEGOCIO1`
    FOREIGN KEY (`idNegocio`)
    REFERENCES `proyecto_david`.`NEGOCIO` (`idNegocio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PEDIDO_USUARIO2`
    FOREIGN KEY (`idRepartidor`)
    REFERENCES `proyecto_david`.`USUARIO` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `proyecto_david`.`DETALLE_PEDIDO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`DETALLE_PEDIDO` (
  `idDetallePedido` INT NOT NULL AUTO_INCREMENT,
  `cantidad` INT NULL,
  `precioUnitario` DECIMAL(10,2) NULL,
  `subtotal` DECIMAL(10,2) NULL,
  `notaEspecial` VARCHAR(100) NULL,
  `idPedido` INT NOT NULL,
  `idProducto` INT NOT NULL,
  PRIMARY KEY (`idDetallePedido`),
  INDEX `fk_DETALLE_PEDIDO_PEDIDO1_idx` (`idPedido` ASC) VISIBLE,
  INDEX `fk_DETALLE_PEDIDO_PRODUCTO1_idx` (`idProducto` ASC) VISIBLE,
  CONSTRAINT `fk_DETALLE_PEDIDO_PEDIDO1`
    FOREIGN KEY (`idPedido`)
    REFERENCES `proyecto_david`.`PEDIDO` (`idPedido`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_DETALLE_PEDIDO_PRODUCTO1`
    FOREIGN KEY (`idProducto`)
    REFERENCES `proyecto_david`.`PRODUCTO` (`idProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `proyecto_david`.`CARRITO` (
  `idCarrito` INT NOT NULL AUTO_INCREMENT,
  `fechaCreacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `fechaActualizacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `estado`varchar(20) NULL,
  `idUsuario` INT NOT NULL,
  `idNegocio` INT NOT NULL,
  PRIMARY KEY (`idCarrito`),
  INDEX `fk_CARRITO_USUARIO1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_CARRITO_NEGOCIO1_idx` (`idNegocio` ASC) VISIBLE,
  CONSTRAINT `fk_CARRITO_USUARIO1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `proyecto_david`.`USUARIO` (`idUsuario`)
    ON DELETE CASCADE -- Si borras al usuario, se borra su carrito
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_CARRITO_NEGOCIO1`
    FOREIGN KEY (`idNegocio`)
    REFERENCES `proyecto_david`.`NEGOCIO` (`idNegocio`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `proyecto_david`.`DETALLE_CARRITO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`DETALLE_CARRITO` (
  `idDetalleCarrito` INT NOT NULL AUTO_INCREMENT,
  `cantidad` INT NOT NULL,
  `precioUnitario` DECIMAL(10,2) NULL,
  `notaEspecial` VARCHAR(100) NULL,
  `idCarrito` INT NOT NULL,
  `idProducto` INT NOT NULL,
  PRIMARY KEY (`idDetalleCarrito`),
  INDEX `fk_DETALLE_CARRITO_CARRITO1_idx` (`idCarrito` ASC) VISIBLE,
  INDEX `fk_DETALLE_CARRITO_PRODUCTO1_idx` (`idProducto` ASC) VISIBLE,
  CONSTRAINT `fk_DETALLE_CARRITO_CARRITO1`
    FOREIGN KEY (`idCarrito`)
    REFERENCES `proyecto_david`.`CARRITO` (`idCarrito`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_DETALLE_CARRITO_PRODUCTO1`
    FOREIGN KEY (`idProducto`)
    REFERENCES `proyecto_david`.`PRODUCTO` (`idProducto`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `proyecto_david`.`PROMOCION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proyecto_david`.`PROMOCION` (
  `idPromocion` INT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) NULL,
  `imagenBanner` VARCHAR(255) NOT NULL,
  `fechaInicio` DATETIME NOT NULL,
  `fechaFin` DATETIME NOT NULL,
  `estado` TINYINT(1) NULL DEFAULT 1,
  `idNegocio` INT NOT NULL,
  `idProducto` INT NULL,
  PRIMARY KEY (`idPromocion`),
  INDEX `fk_PROMOCION_NEGOCIO1_idx` (`idNegocio` ASC) VISIBLE,
  INDEX `fk_PROMOCION_PRODUCTO1_idx` (`idProducto` ASC) VISIBLE,
  CONSTRAINT `fk_PROMOCION_NEGOCIO1`
    FOREIGN KEY (`idNegocio`)
    REFERENCES `proyecto_david`.`NEGOCIO` (`idNegocio`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PROMOCION_PRODUCTO1`
    FOREIGN KEY (`idProducto`)
    REFERENCES `proyecto_david`.`PRODUCTO` (`idProducto`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `proyecto_david`.`DIRECCION_USUARIO`(
    `idDireccionUsuario` INT NOT NULL AUTO_INCREMENT,
    `alias` VARCHAR(30) NOT NULL,
    `direccion` VARCHAR(150) NOT NULL,
    `referencia` VARCHAR(150) NULL,
    `latitud` DECIMAL(10,8) NOT NULL,
    `longitud` DECIMAL(10,8) NOT NULL,
    `principal` TINYINT(1) NOT NULL DEFAULT 0,
    `fechaCreacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `idUsuario` INT NOT NULL,
    PRIMARY KEY (idDireccionUsuario),
    INDEX idx_direccion_usuario (idUsuario),
    CONSTRAINT fk_direccion_usuario
        FOREIGN KEY (idUsuario)
        REFERENCES USUARIO(idUsuario)
        ON DELETE CASCADE
        ON UPDATE NO ACTION) 
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;