package com.proyecto.controller;

import com.proyecto.model.*;
import com.proyecto.repository.*;
import com.proyecto.security.*;
import com.proyecto.services.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/panel")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class PanelController {
  @Autowired private UsuarioRepository usuarioRepo;
  @Autowired private TipoNegocioRepository tipoNegocioRepo;
  @Autowired private NegocioRepository negocioRepo;
  @Autowired private CategoriaProductoRepository categoriaRepo;
  @Autowired private PromocionRepository promocionRepo;
  @Autowired private PedidoRepository pedidoRepo;
  @Autowired private ProductoRepository productoRepo;
  @Autowired private RolRepository rolRepo;
  @Autowired private ZonaRepository zonaRepo;
  @Autowired private CentroComercialRepository centroRepo;
  @Autowired private ConfiguracionRepository configRepo;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private PasswordPolicy passwords;
  @Autowired private FileStorageService storage;
  @Autowired private SecurityAudit audit;

  @Value("${google.maps.api-key:}")
  private String googleMapsApiKey;

  @InitBinder("usuario")
  public void userBinder(WebDataBinder b) {
    b.setAllowedFields(
        "idUsuario",
        "nombres",
        "apellidos",
        "dni",
        "telefono",
        "correoElectronico",
        "password",
        "rol.idRol");
  }

  @InitBinder("negocio")
  public void businessBinder(WebDataBinder b) {
    b.setAllowedFields(
        "idNegocio",
        "nombre",
        "direccion",
        "latitud",
        "longitud",
        "telefono",
        "estado",
        "tipoNegocio.idTipoNegocio",
        "zona.idZona",
        "centroComercial.idCentroComercial",
        "horaInicio",
        "horaFin",
        "modoWhatsapp");
  }

  @InitBinder("producto")
  public void productBinder(WebDataBinder b) {
    b.setAllowedFields(
        "idProducto",
        "nombre",
        "descripcion",
        "precio",
        "disponible",
        "negocio.idNegocio",
        "categoriaProducto.idCategoriaProducto",
        "seccionMenu");
  }

  @InitBinder("categoria")
  public void categoryBinder(WebDataBinder b) {
    b.setAllowedFields("idCategoriaProducto", "nombre", "negocio.idNegocio");
  }

  @InitBinder("promo")
  public void promotionBinder(WebDataBinder b) {
    b.setAllowedFields(
        "idPromocion",
        "titulo",
        "descripcion",
        "fechaInicio",
        "fechaFin",
        "horaInicio",
        "horaFin",
        "mostrarEnIndex",
        "estado",
        "negocio.idNegocio");
  }

  @InitBinder("tipo")
  public void typeBinder(WebDataBinder b) {
    b.setAllowedFields("idTipoNegocio", "nombre", "estado", "icono");
  }

  @InitBinder("config")
  public void configBinder(WebDataBinder b) {
    b.setAllowedFields(
        "telefonoSoporte",
        "linkFacebook",
        "linkInstagram",
        "linkTiktok",
        "mensajeWspCabecera",
        "mensajeWspFooter",
        "precioBaseDelivery",
        "distanciaBaseMetros",
        "precioKmAdicional");
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMINISTRADOR','REPARTIDOR')")
  public String mostrarPanel(
      @RequestParam(required = false) String modulo,
      @RequestParam(name = "q", required = false) String query,
      Authentication auth,
      Model model) {
    boolean admin =
        auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    if (modulo == null || modulo.isBlank())
      return "redirect:/panel?modulo=" + (admin ? "usuarios" : "pedidos");
    if (!admin && !"pedidos".equals(modulo))
      throw new org.springframework.security.access.AccessDeniedException("Acceso denegado");
    model.addAttribute("modulo", modulo);
    switch (modulo) {
      case "usuarios" -> {
        model.addAttribute(
            "listaUsuarios",
            query == null || query.isBlank()
                ? usuarioRepo.findAll()
                : usuarioRepo.buscarPorFiltro(InputChecks.text(query, 80)));
        model.addAttribute("q", query);
        model.addAttribute("listaRoles", rolRepo.findAll());
      }
      case "tipos_negocio" -> model.addAttribute("listaTipos", tipoNegocioRepo.findAll());
      case "negocios" -> {
        model.addAttribute("listaNegocios", negocioRepo.findAll());
        model.addAttribute("listaTipos", tipoNegocioRepo.findAll());
        model.addAttribute("listaZonas", zonaRepo.findAll());
        model.addAttribute("listaCentros", centroRepo.findAll());
        model.addAttribute("googleMapsKey", googleMapsApiKey);
      }
      case "categorias" -> {
        model.addAttribute("listaCategorias", categoriaRepo.findAll());
        model.addAttribute("listaNegocios", negocioRepo.findAll());
      }
      case "productos" -> {
        model.addAttribute("listaProductos", productoRepo.findAll());
        model.addAttribute("listaNegocios", negocioRepo.findAll());
        model.addAttribute("listaCategorias", categoriaRepo.findAll());
      }
      case "promociones" -> {
        model.addAttribute("listaPromociones", promocionRepo.findAll());
        model.addAttribute("listaNegocios", negocioRepo.findAll());
      }
      case "pedidos" -> {
        var u =
            usuarioRepo
                .findByCorreoElectronico(auth.getName())
                .orElseThrow(BusinessException::missing);
        model.addAttribute(
            "listaPedidos",
            admin
                ? pedidoRepo.findAll()
                : pedidoRepo.findByRepartidorIdUsuarioOrderByIdPedidoDesc(u.getIdUsuario()));
      }
      case "configuracion" ->
          model.addAttribute("configApp", configRepo.findById(1).orElse(new Configuracion()));
      default -> throw BusinessException.missing();
    }
    return "Panel";
  }

  private String success(RedirectAttributes flash, String module, String event) {
    flash.addFlashAttribute("mensajeExito", "Cambios guardados correctamente.");
    audit.event(event, SecurityAudit.actor());
    return "redirect:/panel?modulo=" + module;
  }

  @PostMapping("/negocios/guardar")
  public String guardarNegocio(
      @ModelAttribute("negocio") Negocio n,
      @RequestParam(required = false) MultipartFile archivoLogo,
      @RequestParam(required = false) MultipartFile archivoCarta,
      RedirectAttributes flash)
      throws java.io.IOException {
    n.setNombre(InputChecks.text(n.getNombre(), 45));
    n.setDireccion(InputChecks.text(n.getDireccion(), 50));
    InputChecks.phone(n.getTelefono());
    InputChecks.coordinates(n.getLatitud(), n.getLongitud());
    if (n.getTipoNegocio() == null || n.getTipoNegocio().getIdTipoNegocio() == null)
      throw BusinessException.invalid("Selecciona un tipo de negocio.");
    n.setTipoNegocio(
        tipoNegocioRepo
            .findById(n.getTipoNegocio().getIdTipoNegocio())
            .orElseThrow(BusinessException::missing));
    n.setZona(
        n.getZona() == null || n.getZona().getIdZona() == null
            ? null
            : zonaRepo.findById(n.getZona().getIdZona()).orElseThrow(BusinessException::missing));
    n.setCentroComercial(
        n.getCentroComercial() == null || n.getCentroComercial().getIdCentroComercial() == null
            ? null
            : centroRepo
                .findById(n.getCentroComercial().getIdCentroComercial())
                .orElseThrow(BusinessException::missing));
    var old =
        n.getIdNegocio() == null
            ? null
            : negocioRepo.findById(n.getIdNegocio()).orElseThrow(BusinessException::missing);
    try (var logo = storage.storeImage(archivoLogo, "imagenes");
        var carta = storage.storeImage(archivoCarta, "carta")) {
      n.setImagenLogo(
          logo.filename() != null ? logo.filename() : old == null ? null : old.getImagenLogo());
      n.setImagenCarta(
          carta.filename() != null ? carta.filename() : old == null ? null : old.getImagenCarta());
      negocioRepo.saveAndFlush(n);
      logo.commit();
      carta.commit();
    }
    return success(flash, "negocios", "BUSINESS_SAVED");
  }

  @PostMapping("/negocios/eliminar")
  public String eliminarNegocio(@RequestParam Integer idNegocio, RedirectAttributes f) {
    negocioRepo.deleteById(idNegocio);
    return success(f, "negocios", "BUSINESS_DELETED");
  }

  @PostMapping("/usuarios/guardar")
  public String guardarUsuario(
      @ModelAttribute("usuario") Usuario form, Authentication auth, RedirectAttributes f) {
    String email = UserAccountService.normalizeEmail(form.getCorreoElectronico());
    if (email.length() > 45 || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
      throw BusinessException.invalid("Correo no válido.");
    InputChecks.text(form.getNombres(), 45);
    InputChecks.text(form.getApellidos(), 45);
    InputChecks.phone(form.getTelefono());
    if (form.getDni() != null && !form.getDni().isBlank() && !form.getDni().matches("[0-9]{8}"))
      throw BusinessException.invalid("DNI no válido.");
    Usuario u =
        form.getIdUsuario() == null
            ? new Usuario()
            : usuarioRepo.findById(form.getIdUsuario()).orElseThrow(BusinessException::missing);
    if (usuarioRepo
            .findByCorreoElectronico(email)
            .filter(x -> !x.getIdUsuario().equals(u.getIdUsuario()))
            .isPresent()
        || usuarioRepo
            .findByTelefono(form.getTelefono())
            .filter(x -> !x.getIdUsuario().equals(u.getIdUsuario()))
            .isPresent()
        || (form.getDni() != null
            && !form.getDni().isBlank()
            && usuarioRepo
                .findByDni(form.getDni())
                .filter(x -> !x.getIdUsuario().equals(u.getIdUsuario()))
                .isPresent()))
      throw BusinessException.invalid("Correo, teléfono o DNI ya registrado.");
    if (form.getRol() == null
        || form.getRol().getIdRol() == null
        || !Set.of(1, 2, 3).contains(form.getRol().getIdRol()))
      throw BusinessException.invalid("Rol no válido.");
    boolean sensitive =
        u.getIdUsuario() != null
            && (!Objects.equals(u.getCorreoElectronico(), email)
                || !Objects.equals(u.getRol().getIdRol(), form.getRol().getIdRol()));
    if (form.getPassword() != null && !form.getPassword().isBlank()) {
      passwords.validate(form.getPassword());
      u.setPassword(passwordEncoder.encode(form.getPassword()));
      sensitive = u.getIdUsuario() != null;
    } else if (u.getIdUsuario() == null)
      throw BusinessException.invalid("La contraseña es obligatoria.");
    u.setNombres(form.getNombres().strip());
    u.setApellidos(form.getApellidos().strip());
    u.setTelefono(form.getTelefono());
    u.setDni(form.getDni() == null || form.getDni().isBlank() ? null : form.getDni());
    u.setCorreoElectronico(email);
    u.setRol(rolRepo.findById(form.getRol().getIdRol()).orElseThrow(BusinessException::missing));
    if (sensitive) UserAccountService.revoke(u);
    usuarioRepo.saveAndFlush(u);
    audit.alert(
        "ADMIN_USER_OR_ROLE_CHANGED", SecurityAudit.actor() + " target=" + u.getIdUsuario());
    return success(f, "usuarios", "USER_SAVED");
  }

  @PostMapping("/usuarios/eliminar")
  public String eliminarUsuario(
      @RequestParam Integer idUsuario, Authentication auth, RedirectAttributes f) {
    var u = usuarioRepo.findById(idUsuario).orElseThrow(BusinessException::missing);
    if (u.getCorreoElectronico().equals(auth.getName()))
      throw BusinessException.invalid("No puedes eliminar tu propia cuenta.");
    usuarioRepo.delete(u);
    audit.alert("ADMIN_USER_DELETED", SecurityAudit.actor() + " target=" + idUsuario);
    return success(f, "usuarios", "USER_DELETED");
  }

  @PostMapping("/categorias/guardar")
  public String guardarCategoria(
      @ModelAttribute("categoria") CategoriaProducto c,
      @RequestParam(required = false) MultipartFile archivoLogo,
      RedirectAttributes f)
      throws java.io.IOException {
    c.setNombre(InputChecks.text(c.getNombre(), 45));
    if (c.getNegocio() == null || c.getNegocio().getIdNegocio() == null)
      throw BusinessException.invalid("Selecciona el negocio.");
    c.setNegocio(
        negocioRepo
            .findById(c.getNegocio().getIdNegocio())
            .orElseThrow(BusinessException::missing));
    var old =
        c.getIdCategoriaProducto() == null
            ? null
            : categoriaRepo
                .findById(c.getIdCategoriaProducto())
                .orElseThrow(BusinessException::missing);
    try (var image = storage.storeImage(archivoLogo, "categorias_comidas")) {
      c.setLogo(image.filename() != null ? image.filename() : old == null ? null : old.getLogo());
      categoriaRepo.saveAndFlush(c);
      image.commit();
    }
    return success(f, "categorias", "CATEGORY_SAVED");
  }

  @PostMapping("/categorias/eliminar")
  public String eliminarCategoria(@RequestParam Integer idCategoriaProducto, RedirectAttributes f) {
    categoriaRepo.deleteById(idCategoriaProducto);
    return success(f, "categorias", "CATEGORY_DELETED");
  }

  @PostMapping("/productos/guardar")
  public String guardarProducto(
      @ModelAttribute("producto") Producto p,
      @RequestParam(required = false) MultipartFile archivoImagen,
      RedirectAttributes f)
      throws java.io.IOException {
    p.setNombre(InputChecks.text(p.getNombre(), 45));
    InputChecks.optional(p.getDescripcion(), 255);
    InputChecks.optional(p.getSeccionMenu(), 45);
    if (p.getSeccionMenu() == null) p.setSeccionMenu("PRINCIPAL");
    if (!Set.of("RECOMENDADO", "PRINCIPAL", "BEBIDA", "COMPLEMENTO").contains(p.getSeccionMenu()))
      throw BusinessException.invalid("Sección del menú no válida.");
    InputChecks.money(p.getPrecio());
    if (p.getNegocio() == null
        || p.getNegocio().getIdNegocio() == null
        || p.getCategoriaProducto() == null
        || p.getCategoriaProducto().getIdCategoriaProducto() == null)
      throw BusinessException.invalid("Selecciona el negocio y la categoría.");
    p.setNegocio(
        negocioRepo
            .findById(p.getNegocio().getIdNegocio())
            .orElseThrow(BusinessException::missing));
    p.setCategoriaProducto(
        categoriaRepo
            .findById(p.getCategoriaProducto().getIdCategoriaProducto())
            .orElseThrow(BusinessException::missing));
    if (!p.getCategoriaProducto().getNegocio().getIdNegocio().equals(p.getNegocio().getIdNegocio()))
      throw BusinessException.invalid("La categoría no pertenece al negocio.");
    var old =
        p.getIdProducto() == null
            ? null
            : productoRepo.findById(p.getIdProducto()).orElseThrow(BusinessException::missing);
    try (var image = storage.storeImage(archivoImagen, "productos")) {
      p.setImagen(
          image.filename() != null ? image.filename() : old == null ? null : old.getImagen());
      productoRepo.saveAndFlush(p);
      image.commit();
    }
    return success(f, "productos", "PRODUCT_SAVED");
  }

  @PostMapping("/productos/eliminar")
  public String eliminarProducto(@RequestParam Integer idProducto, RedirectAttributes f) {
    productoRepo.deleteById(idProducto);
    return success(f, "productos", "PRODUCT_DELETED");
  }

  @PostMapping("/promociones/guardar")
  public String guardarPromocion(
      @ModelAttribute("promo") Promocion p,
      @RequestParam(required = false) List<String> dias,
      @RequestParam(required = false) MultipartFile archivoBanner,
      RedirectAttributes f)
      throws java.io.IOException {
    p.setTitulo(InputChecks.text(p.getTitulo(), 100));
    InputChecks.optional(p.getDescripcion(), 255);
    if (p.getFechaInicio() != null
        && p.getFechaFin() != null
        && p.getFechaFin().isBefore(p.getFechaInicio()))
      throw BusinessException.invalid("Revisa el rango de fechas.");
    if (dias != null && dias.stream().anyMatch(d -> !d.matches("[1-7]")))
      throw BusinessException.invalid("Día no válido.");
    p.setDiasActivos(dias == null ? "" : String.join(",", new LinkedHashSet<>(dias)));
    if (p.getMostrarEnIndex() == null) p.setMostrarEnIndex(false);
    if (p.getNegocio() == null || p.getNegocio().getIdNegocio() == null)
      throw BusinessException.invalid("Selecciona un negocio.");
    p.setNegocio(
        negocioRepo
            .findById(p.getNegocio().getIdNegocio())
            .orElseThrow(BusinessException::missing));
    var old =
        p.getIdPromocion() == null
            ? null
            : promocionRepo.findById(p.getIdPromocion()).orElseThrow(BusinessException::missing);
    try (var image = storage.storeImage(archivoBanner, "promociones")) {
      p.setImagenBanner(
          image.filename() != null ? image.filename() : old == null ? null : old.getImagenBanner());
      promocionRepo.saveAndFlush(p);
      image.commit();
    }
    return success(f, "promociones", "PROMOTION_SAVED");
  }

  @PostMapping("/promociones/eliminar")
  public String eliminarPromocion(@RequestParam Integer idPromocion, RedirectAttributes f) {
    promocionRepo.deleteById(idPromocion);
    return success(f, "promociones", "PROMOTION_DELETED");
  }

  @PostMapping("/tipos_negocio/ocultar")
  public String ocultarTipoNegocio(@RequestParam Integer idTipoNegocio, RedirectAttributes f) {
    var t = tipoNegocioRepo.findById(idTipoNegocio).orElseThrow(BusinessException::missing);
    t.setEstado(false);
    tipoNegocioRepo.saveAndFlush(t);
    return success(f, "tipos_negocio", "BUSINESS_TYPE_HIDDEN");
  }

  @PostMapping("/tipos_negocio/guardar")
  public String guardarTipoNegocio(@ModelAttribute("tipo") TipoNegocio t, RedirectAttributes f) {
    t.setNombre(InputChecks.text(t.getNombre(), 45));
    InputChecks.optional(t.getIcono(), 200);
    if (t.getIdTipoNegocio() != null && !tipoNegocioRepo.existsById(t.getIdTipoNegocio()))
      throw BusinessException.missing();
    if (t.getEstado() == null) t.setEstado(true);
    tipoNegocioRepo.saveAndFlush(t);
    return success(f, "tipos_negocio", "BUSINESS_TYPE_SAVED");
  }

  @PostMapping("/configuracion/guardar")
  public String guardarConfiguracion(
      @ModelAttribute("config") Configuracion c, RedirectAttributes f) {
    InputChecks.phone(c.getTelefonoSoporte());
    InputChecks.httpsUrl(c.getLinkFacebook());
    InputChecks.httpsUrl(c.getLinkInstagram());
    InputChecks.httpsUrl(c.getLinkTiktok());
    InputChecks.optional(c.getMensajeWspCabecera(), 500);
    InputChecks.optional(c.getMensajeWspFooter(), 500);
    if (c.getPrecioBaseDelivery() == null
        || c.getPrecioKmAdicional() == null
        || c.getDistanciaBaseMetros() == null
        || c.getDistanciaBaseMetros() < 0
        || c.getDistanciaBaseMetros() > 1000000)
      throw BusinessException.invalid("Revisa las tarifas de delivery.");
    InputChecks.money(c.getPrecioBaseDelivery());
    InputChecks.money(c.getPrecioKmAdicional());
    c.setIdConfiguracion(1);
    configRepo.saveAndFlush(c);
    return success(f, "configuracion", "CONFIG_CHANGED");
  }
}
