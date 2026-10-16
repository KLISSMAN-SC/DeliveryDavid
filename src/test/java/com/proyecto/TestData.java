package com.proyecto;

import com.proyecto.model.*;
import com.proyecto.repository.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

final class TestData {
  static final String PASSWORD = "PruebaSegura!2026";

  record Seed(
      Usuario admin,
      Usuario client,
      Usuario other,
      Usuario courier,
      Negocio negocio,
      Producto producto,
      DireccionUsuario direccion) {}

  static Seed reset(ApplicationContext c) {
    var jdbc = c.getBean(JdbcTemplate.class);
    jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");
    for (String table :
        List.of(
            "DETALLE_PEDIDO",
            "PEDIDO",
            "DETALLE_CARRITO",
            "CARRITO",
            "DIRECCION_USUARIO",
            "PROMOCION",
            "PRODUCTO",
            "CATEGORIAS_PRODUCTO",
            "NEGOCIO",
            "CENTRO_COMERCIAL",
            "ZONA",
            "USUARIO",
            "ROL",
            "TIPO_NEGOCIO",
            "CONFIGURACION")) jdbc.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY");
    jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");
    jdbc.update(
        "INSERT INTO ROL(idRol,nombre) VALUES(1,'Administrador'),(2,'Cliente'),(3,'Repartidor')");
    var roles = c.getBean(RolRepository.class);
    var users = c.getBean(UsuarioRepository.class);
    var encoder = c.getBean(PasswordEncoder.class);
    List<Usuario> list = new ArrayList<>();
    int phone = 900000001;
    String fixtureHash = encoder.encode(PASSWORD);
    for (String name : List.of("admin", "cliente", "otro", "repartidor")) {
      var u = new Usuario();
      u.setNombres(name);
      u.setApellidos("Prueba");
      u.setTelefono(Integer.toString(phone++));
      u.setCorreoElectronico(name + "@example.invalid");
      u.setPassword(fixtureHash);
      u.setRol(
          roles
              .findById(name.equals("admin") ? 1 : name.equals("repartidor") ? 3 : 2)
              .orElseThrow());
      u.setIntentosFallidos(0);
      list.add(users.saveAndFlush(u));
    }
    var type = new TipoNegocio();
    type.setNombre("Restaurante");
    type.setEstado(true);
    type = c.getBean(TipoNegocioRepository.class).saveAndFlush(type);
    var n = new Negocio();
    n.setNombre("Restaurante de prueba");
    n.setDireccion("Dirección de prueba");
    n.setEstado(Negocio.EstadoNegocio.ABIERTO);
    n.setTipoNegocio(type);
    n.setTelefono("900000099");
    n.setLatitud(new BigDecimal("-12.05000000"));
    n.setLongitud(new BigDecimal("-77.05000000"));
    n = c.getBean(NegocioRepository.class).saveAndFlush(n);
    var category = new CategoriaProducto();
    category.setNombre("Platos");
    category.setNegocio(n);
    category = c.getBean(CategoriaProductoRepository.class).saveAndFlush(category);
    var p = new Producto();
    p.setNombre("Producto de prueba");
    p.setSeccionMenu("PRINCIPAL");
    p.setPrecio(new BigDecimal("10.00"));
    p.setDisponible(true);
    p.setNegocio(n);
    p.setCategoriaProducto(category);
    p = c.getBean(ProductoRepository.class).saveAndFlush(p);
    var a = new DireccionUsuario();
    a.setAlias("Casa");
    a.setDireccion("Dirección privada del cliente");
    a.setUsuario(list.get(1));
    a.setLatitud(n.getLatitud());
    a.setLongitud(n.getLongitud());
    a = c.getBean(DireccionUsuarioRepository.class).saveAndFlush(a);
    var config = new Configuracion();
    config.setTelefonoSoporte("900000099");
    config.setPrecioBaseDelivery(new BigDecimal("10.00"));
    config.setDistanciaBaseMetros(500);
    config.setPrecioKmAdicional(new BigDecimal("1.00"));
    c.getBean(ConfiguracionRepository.class).saveAndFlush(config);
    return new Seed(list.get(0), list.get(1), list.get(2), list.get(3), n, p, a);
  }
}
