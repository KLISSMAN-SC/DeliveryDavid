package db.migration;

import java.sql.*;
import java.util.*;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

/** Migración para el esquema original del repositorio. MySQL 8.0.16+ / 8.4. */
public class V1__Security extends BaseJavaMigration {
  @Override
  public boolean canExecuteInTransaction() {
    return false;
  }

  private void assertEmpty(Connection c, String sql, String message) throws SQLException {
    try (var s = c.createStatement();
        var r = s.executeQuery(sql)) {
      if (r.next())
        throw new SQLException(
            message
                + " Revisa ProyectoBD/01_PRECOMPROBACIONES.sql y realiza una copia de seguridad.");
    }
  }

  private void execute(Connection c, String sql) throws SQLException {
    try (var s = c.createStatement()) {
      s.execute(sql);
    }
  }

  private void addColumn(Connection c, String table, String column, String definition)
      throws SQLException {
    try (var r = c.getMetaData().getColumns(c.getCatalog(), null, table, column)) {
      if (r.next()) return;
    }
    execute(c, "ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
  }

  @Override
  public void migrate(Context context) throws Exception {
    Connection c = context.getConnection();
    if (!c.getMetaData().getDatabaseProductName().contains("MySQL"))
      throw new SQLException(
          "Esta migración requiere MySQL. No ejecutar en una base H2 o MariaDB de producción.");
    String[] tables = {
      "ROL",
      "USUARIO",
      "TIPO_NEGOCIO",
      "ZONA",
      "CENTRO_COMERCIAL",
      "NEGOCIO",
      "CATEGORIAS_PRODUCTO",
      "PRODUCTO",
      "PEDIDO",
      "DETALLE_PEDIDO",
      "CARRITO",
      "DETALLE_CARRITO",
      "PROMOCION",
      "DIRECCION_USUARIO",
      "CONFIGURACION"
    };
    Map<String, String> actual = new HashMap<>();
    try (var r = c.getMetaData().getTables(c.getCatalog(), null, "%", new String[] {"TABLE"})) {
      while (r.next())
        actual.put(r.getString("TABLE_NAME").toUpperCase(Locale.ROOT), r.getString("TABLE_NAME"));
    }
    for (String t : tables)
      if (!actual.containsKey(t))
        throw new SQLException(
            "Falta la tabla "
                + t
                + ". Importa primero el DDL original en una base nueva; no lo ejecutes para"
                + " reemplazar una base existente.");
    // Las comprobaciones se ejecutan antes de cualquier UPDATE/ALTER.
    for (String field : new String[] {"correoElectronico", "telefono", "dni"}) {
      String expr =
          field.equals("correoElectronico")
              ? "LOWER(TRIM(`" + field + "`))"
              : "TRIM(`" + field + "`)";
      assertEmpty(
          c,
          "SELECT "
              + expr
              + " FROM `"
              + actual.get("USUARIO")
              + "` WHERE "
              + expr
              + " IS NOT NULL AND "
              + expr
              + "<>'' GROUP BY "
              + expr
              + " HAVING COUNT(*)>1",
          "Hay valores duplicados en " + field + ".");
    }
    assertEmpty(
        c,
        "SELECT idUsuario FROM `"
            + actual.get("USUARIO")
            + "` WHERE correoElectronico IS NULL OR TRIM(correoElectronico)='' OR password IS NULL",
        "Hay cuentas sin correo o contraseña.");
    assertEmpty(
        c,
        "SELECT idPedido FROM `"
            + actual.get("PEDIDO")
            + "` WHERE total<0 OR propina<0 OR costoEnvio<0",
        "Hay pedidos con importes negativos.");
    assertEmpty(
        c,
        "SELECT idProducto FROM `" + actual.get("PRODUCTO") + "` WHERE precio<0",
        "Hay productos con precios negativos.");
    assertEmpty(
        c,
        "SELECT idDetalleCarrito FROM `"
            + actual.get("DETALLE_CARRITO")
            + "` WHERE cantidad<1 OR cantidad>99 OR precioUnitario<0",
        "Hay carritos con cantidades o precios inválidos.");
    assertEmpty(
        c,
        "SELECT idUsuario,idNegocio FROM `"
            + actual.get("CARRITO")
            + "` WHERE estado='ACTIVO' GROUP BY idUsuario,idNegocio HAVING COUNT(*)>1",
        "Hay carritos activos duplicados.");
    assertEmpty(
        c,
        "SELECT idDireccionUsuario FROM `"
            + actual.get("DIRECCION_USUARIO")
            + "` WHERE latitud NOT BETWEEN -90 AND 90 OR longitud NOT BETWEEN -180 AND 180",
        "Hay direcciones con coordenadas inválidas.");
    assertEmpty(
        c,
        "SELECT idNegocio FROM `"
            + actual.get("NEGOCIO")
            + "` WHERE latitud NOT BETWEEN -90 AND 90 OR longitud NOT BETWEEN -180 AND 180",
        "Hay negocios con coordenadas inválidas.");
    assertEmpty(
        c,
        "SELECT idConfiguracion FROM `"
            + actual.get("CONFIGURACION")
            + "` WHERE precioBaseDelivery<0 OR precioKmAdicional<0 OR distanciaBaseMetros<0",
        "Hay tarifas negativas.");
    assertEmpty(
        c,
        "SELECT idConfiguracion FROM `"
            + actual.get("CONFIGURACION")
            + "` WHERE precioBaseDelivery>99999999.99 OR precioKmAdicional>99999999.99 OR"
            + " ABS(precioBaseDelivery-ROUND(precioBaseDelivery,2))>0.0000001 OR"
            + " ABS(precioKmAdicional-ROUND(precioKmAdicional,2))>0.0000001",
        "Hay tarifas que deben revisarse antes de convertirlas a DECIMAL(10,2).");
    // Corrige diferencias de mayúsculas únicamente en servidores sensibles al nombre.
    try (var s = c.createStatement();
        var r = s.executeQuery("SELECT @@lower_case_table_names")) {
      r.next();
      if (r.getInt(1) == 0)
        for (String t : tables)
          if (!t.equals(actual.get(t)))
            execute(c, "RENAME TABLE `" + actual.get(t) + "` TO `" + t + "`");
    }
    execute(
        c,
        "UPDATE USUARIO SET correoElectronico=LOWER(TRIM(correoElectronico)),"
            + " telefono=NULLIF(TRIM(telefono),''), dni=NULLIF(TRIM(dni),''), tokenPassword=NULL");
    addColumn(c, "USUARIO", "tokenPasswordExpira", "DATETIME(6) NULL");
    addColumn(c, "USUARIO", "securityVersion", "BIGINT NOT NULL DEFAULT 0");
    addColumn(c, "PEDIDO", "idCarritoOrigen", "INT NULL");
    addColumn(
        c,
        "CARRITO",
        "activoUnico",
        "TINYINT GENERATED ALWAYS AS (CASE WHEN estado='ACTIVO' THEN 1 ELSE NULL END) VIRTUAL");
    execute(
        c,
        "ALTER TABLE USUARIO ADD CONSTRAINT uk_usuario_correo UNIQUE(correoElectronico), ADD"
            + " CONSTRAINT uk_usuario_telefono UNIQUE(telefono), ADD CONSTRAINT uk_usuario_dni"
            + " UNIQUE(dni)");
    execute(
        c,
        "ALTER TABLE PEDIDO ADD CONSTRAINT uk_pedido_carrito UNIQUE(idCarritoOrigen), ADD"
            + " CONSTRAINT ck_pedido_importes CHECK(total>=0 AND propina>=0 AND costoEnvio>=0),"
            + " MODIFY longitudEnvio DECIMAL(11,8) NULL");
    execute(
        c,
        "ALTER TABLE CARRITO ADD CONSTRAINT uk_carrito_activo"
            + " UNIQUE(idUsuario,idNegocio,activoUnico)");
    execute(
        c,
        "ALTER TABLE PRODUCTO ADD CONSTRAINT ck_producto_precio CHECK(precio>=0), MODIFY"
            + " seccionMenu VARCHAR(45) NULL DEFAULT 'PRINCIPAL'");
    execute(
        c,
        "ALTER TABLE DETALLE_CARRITO ADD CONSTRAINT ck_detalle_cantidad CHECK(cantidad BETWEEN 1"
            + " AND 99), ADD CONSTRAINT ck_detalle_precio CHECK(precioUnitario>=0)");
    execute(
        c,
        "ALTER TABLE DIRECCION_USUARIO MODIFY longitud DECIMAL(11,8) NOT NULL, ADD CONSTRAINT"
            + " ck_direccion_geo CHECK(latitud BETWEEN -90 AND 90 AND longitud BETWEEN -180 AND"
            + " 180)");
    execute(
        c,
        "ALTER TABLE NEGOCIO MODIFY longitud DECIMAL(11,8) NULL, ADD CONSTRAINT ck_negocio_geo"
            + " CHECK(latitud BETWEEN -90 AND 90 AND longitud BETWEEN -180 AND 180)");
    execute(
        c,
        "ALTER TABLE CONFIGURACION MODIFY precioBaseDelivery DECIMAL(10,2) NULL, MODIFY"
            + " precioKmAdicional DECIMAL(10,2) NULL, ADD CONSTRAINT ck_tarifas"
            + " CHECK(precioBaseDelivery>=0 AND precioKmAdicional>=0 AND distanciaBaseMetros>=0)");
  }
}
