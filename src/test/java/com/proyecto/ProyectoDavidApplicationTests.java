package com.proyecto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.proyecto.model.*;
import com.proyecto.repository.*;
import com.proyecto.security.*;
import com.proyecto.services.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class ProyectoDavidApplicationTests {
  @Autowired WebApplicationContext context;
  @Autowired UsuarioRepository users;
  @Autowired PedidoRepository orders;
  @Autowired CarritoService carts;
  @Autowired PasswordEncoder encoder;
  @Autowired ProductoRepository products;
  @MockitoBean EmailService email;
  MockMvc mvc;
  TestData.Seed data;
  AtomicReference<String> code;

  @BeforeEach
  void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    data = TestData.reset(context);
    code = new AtomicReference<>();
    doAnswer(
            i -> {
              code.set(i.getArgument(1));
              return null;
            })
        .when(email)
        .enviarCorreoVerificacion(anyString(), anyString(), anyString());
  }

  @Test
  void courierCannotWriteAdministrativeUsers() throws Exception {
    mvc.perform(
            post("/panel/usuarios/guardar")
                .with(user(data.courier().getCorreoElectronico()).roles("REPARTIDOR"))
                .with(csrf())
                .param("nombres", "Nuevo")
                .param("rol.idRol", "1"))
        .andExpect(status().isForbidden());
    assertEquals(4, users.count());
  }

  @Test
  void csrfIsRequiredForProfile() throws Exception {
    mvc.perform(post("/perfil/actualizar").with(user(data.client().getCorreoElectronico())))
        .andExpect(status().isForbidden());
  }

  @Test
  void anonymousCannotReadCart() throws Exception {
    mvc.perform(
            get("/carrito/actual")
                .param("idNegocio", "1")
                .header("X-Requested-With", "XMLHttpRequest"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void foreignOrderIsNotDisclosed() throws Exception {
    var cart =
        carts.agregarProducto(
            data.client().getIdUsuario(),
            data.negocio().getIdNegocio(),
            data.producto().getIdProducto());
    var p =
        context
            .getBean(PedidoService.class)
            .confirmarPedido(
                data.client().getIdUsuario(),
                data.negocio().getIdNegocio(),
                data.direccion().getIdDireccionUsuario(),
                "EFECTIVO",
                BigDecimal.ZERO,
                cart.getIdCarrito());
    var result =
        mvc.perform(
                get("/pedido/confirmacion/" + p.getIdPedido())
                    .with(user(data.other().getCorreoElectronico())))
            .andExpect(status().isNotFound())
            .andReturn();
    assertFalse(
        result.getResponse().getContentAsString().contains("Dirección privada del cliente"));
  }

  @Test
  void cartResponseExcludesUserSecrets() throws Exception {
    carts.agregarProducto(
        data.client().getIdUsuario(),
        data.negocio().getIdNegocio(),
        data.producto().getIdProducto());
    var r =
        mvc.perform(
                get("/carrito/actual")
                    .param("idNegocio", data.negocio().getIdNegocio().toString())
                    .with(user(data.client().getCorreoElectronico())))
            .andExpect(status().isOk())
            .andReturn();
    String body = r.getResponse().getContentAsString();
    assertTrue(body.contains("Producto de prueba"));
    assertFalse(body.contains("password"));
    assertFalse(body.contains("tokenPassword"));
    assertFalse(body.contains("correoElectronico"));
  }

  @Test
  void negativeTipRejected() throws Exception {
    mvc.perform(
            post("/pedido/confirmar")
                .with(user(data.client().getCorreoElectronico()))
                .with(csrf())
                .header("X-Requested-With", "XMLHttpRequest")
                .contentType("application/x-www-form-urlencoded")
                .param("idNegocio", "1")
                .param("idDireccion", "1")
                .param("metodoPago", "EFECTIVO")
                .param("propina", "-20"))
        .andExpect(status().isBadRequest());
    assertEquals(0, orders.count());
  }

  @Test
  void invalidPaymentRejected() throws Exception {
    mvc.perform(
            post("/pedido/confirmar")
                .with(user(data.client().getCorreoElectronico()))
                .with(csrf())
                .header("X-Requested-With", "XMLHttpRequest")
                .contentType("application/x-www-form-urlencoded")
                .param("idNegocio", "1")
                .param("idDireccion", "1")
                .param("metodoPago", "PAGO_FALSO"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void orderRetryReturnsSameOrder() throws Exception {
    var cart =
        carts.agregarProducto(
            data.client().getIdUsuario(),
            data.negocio().getIdNegocio(),
            data.producto().getIdProducto());
    var service = context.getBean(PedidoService.class);
    var first =
        service.confirmarPedido(
            data.client().getIdUsuario(),
            data.negocio().getIdNegocio(),
            data.direccion().getIdDireccionUsuario(),
            "EFECTIVO",
            BigDecimal.ZERO,
            cart.getIdCarrito());
    var second =
        service.confirmarPedido(
            data.client().getIdUsuario(),
            data.negocio().getIdNegocio(),
            data.direccion().getIdDireccionUsuario(),
            "EFECTIVO",
            BigDecimal.ZERO,
            cart.getIdCarrito());
    assertEquals(first.getIdPedido(), second.getIdPedido());
    assertEquals(1, orders.count());
  }

  @Test
  void unavailableProductCannotBeOrdered() {
    data.producto().setDisponible(false);
    products.saveAndFlush(data.producto());
    assertThrows(
        BusinessException.class,
        () ->
            carts.agregarProducto(
                data.client().getIdUsuario(),
                data.negocio().getIdNegocio(),
                data.producto().getIdProducto()));
  }

  @Test
  void resetExpiresAndDoesNotChangePassword() throws Exception {
    String token = TokenTools.token();
    var u = data.client();
    u.setTokenPassword(TokenTools.hash(token));
    u.setTokenPasswordExpira(Instant.now().minusSeconds(1));
    users.saveAndFlush(u);
    mvc.perform(get("/recuperar-password/reset").param("token", token))
        .andExpect(redirectedUrl("/login?errorToken=true"));
    assertTrue(
        encoder.matches(
            TestData.PASSWORD, users.findById(u.getIdUsuario()).orElseThrow().getPassword()));
  }

  @Test
  void resetIsSingleUseAndRevokesSessions() {
    String token = TokenTools.token();
    var u = data.client();
    u.setTokenPassword(TokenTools.hash(token));
    u.setTokenPasswordExpira(Instant.now().plusSeconds(900));
    users.saveAndFlush(u);
    var service = context.getBean(UserAccountService.class);
    service.reset(token, "NuevaClaveSegura!2026");
    assertThrows(BusinessException.class, () -> service.reset(token, "OtraClaveSegura!2026"));
    u = users.findById(u.getIdUsuario()).orElseThrow();
    assertNull(u.getTokenPassword());
    assertEquals(1L, u.getSecurityVersion());
    assertTrue(encoder.matches("NuevaClaveSegura!2026", u.getPassword()));
  }

  @Test
  void profilePasswordRequiresCurrentPassword() throws Exception {
    mvc.perform(
            post("/perfil/actualizar")
                .with(user(data.client().getCorreoElectronico()))
                .with(csrf())
                .param("nombres", "Cliente")
                .param("apellidos", "Prueba")
                .param("telefono", data.client().getTelefono())
                .param("password", "NuevaClaveSegura!2026")
                .param("passwordActual", "Incorrecta!"))
        .andExpect(redirectedUrl("/perfil"));
    assertTrue(
        encoder.matches(
            TestData.PASSWORD,
            users.findById(data.client().getIdUsuario()).orElseThrow().getPassword()));
  }

  @Test
  void oldSessionIsRejectedAfterVersionChange() throws Exception {
    var principal = new AppUserPrincipal(data.client(), "CLIENTE");
    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    var security = SecurityContextHolder.createEmptyContext();
    security.setAuthentication(auth);
    var session = new MockHttpSession();
    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, security);
    var u = data.client();
    UserAccountService.revoke(u);
    users.saveAndFlush(u);
    mvc.perform(get("/perfil").session(session).header("X-Requested-With", "XMLHttpRequest"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void weakPasswordRejectedOnServer() throws Exception {
    mvc.perform(
            post("/registro/enviar")
                .with(csrf())
                .param("nombres", "Nuevo")
                .param("apellidos", "Cliente")
                .param("telefono", "933333333")
                .param("correoElectronico", "nuevo@example.invalid")
                .param("password", "x"))
        .andExpect(redirectedUrl("/registro"));
    verify(email, never()).enviarCorreoVerificacion(anyString(), anyString(), anyString());
  }

  @Test
  void registrationCannotOverwriteAnExistingIdAndRotatesSession() throws Exception {
    var session = new MockHttpSession();
    String before = session.getId();
    mvc.perform(
            post("/registro/enviar")
                .session(session)
                .with(csrf())
                .param("idUsuario", data.admin().getIdUsuario().toString())
                .param("rol.idRol", "1")
                .param("tokenPassword", "attacker")
                .param("nombres", "Nuevo")
                .param("apellidos", "Cliente")
                .param("telefono", "944444444")
                .param("correoElectronico", "nuevo@example.invalid")
                .param("password", TestData.PASSWORD))
        .andExpect(redirectedUrl("/verificar"));
    assertNotNull(code.get());
    mvc.perform(
            post("/verificar/confirmar")
                .session(session)
                .with(csrf())
                .param("codigoIngresado", code.get()))
        .andExpect(redirectedUrl("/"));
    assertNotEquals(before, session.getId());
    var created = users.findByCorreoElectronico("nuevo@example.invalid").orElseThrow();
    assertNotEquals(data.admin().getIdUsuario(), created.getIdUsuario());
    assertEquals(2, created.getRol().getIdRol());
    assertNull(created.getTokenPassword());
    assertEquals(
        data.admin().getCorreoElectronico(),
        users.findById(data.admin().getIdUsuario()).orElseThrow().getCorreoElectronico());
  }

  @Test
  void csrfTokenAndNonceAreRendered() throws Exception {
    var r = mvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
    assertTrue(
        r.getResponse().getHeader("Content-Security-Policy").contains("script-src-attr 'none'"));
    assertTrue(r.getResponse().getContentAsString().contains("_csrf"));
  }

  @Test
  void concurrentConfirmationsProduceOnlyOneOrder() throws Exception {
    var cart =
        carts.agregarProducto(
            data.client().getIdUsuario(),
            data.negocio().getIdNegocio(),
            data.producto().getIdProducto());
    var service = context.getBean(PedidoService.class);
    try (var pool = java.util.concurrent.Executors.newFixedThreadPool(2)) {
      var start = new java.util.concurrent.CountDownLatch(1);
      java.util.concurrent.Callable<Integer> task =
          () -> {
            start.await();
            return service
                .confirmarPedido(
                    data.client().getIdUsuario(),
                    data.negocio().getIdNegocio(),
                    data.direccion().getIdDireccionUsuario(),
                    "EFECTIVO",
                    BigDecimal.ZERO,
                    cart.getIdCarrito())
                .getIdPedido();
          };
      var a = pool.submit(task);
      var b = pool.submit(task);
      start.countDown();
      assertEquals(
          a.get(10, java.util.concurrent.TimeUnit.SECONDS),
          b.get(10, java.util.concurrent.TimeUnit.SECONDS));
      assertEquals(1, orders.count());
    }
  }

  @Test
  void administratorCanCreateAUser() throws Exception {
    mvc.perform(
            post("/panel/usuarios/guardar")
                .with(user(data.admin().getCorreoElectronico()).roles("ADMINISTRADOR"))
                .with(csrf())
                .param("nombres", "Cuenta")
                .param("apellidos", "Nueva")
                .param("correoElectronico", "cuenta@example.invalid")
                .param("telefono", "955555555")
                .param("dni", "12345678")
                .param("rol.idRol", "2")
                .param("password", TestData.PASSWORD))
        .andExpect(status().is3xxRedirection());
    assertEquals(
        2,
        users.findByCorreoElectronico("cuenta@example.invalid").orElseThrow().getRol().getIdRol());
  }

  @Test
  void administratorPanelRendersWithCspSafeEvents() throws Exception {
    var response =
        mvc.perform(
                get("/panel")
                    .param("modulo", "usuarios")
                    .with(user(data.admin().getCorreoElectronico()).roles("ADMINISTRADOR")))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
    assertTrue(response.getContentAsString().contains("data-click=\"abrirModalUsuario\""));
    assertFalse(response.getContentAsString().contains("onclick="));
  }

  @Test
  void localStylesAndSecurityHelperArePublic() throws Exception {
    mvc.perform(get("/vendor/fontawesome/css/all.min.css")).andExpect(status().isOk());
    mvc.perform(get("/js/security.js")).andExpect(status().isOk());
    mvc.perform(get("/banner/missing.png")).andExpect(status().isNotFound());
  }
}
