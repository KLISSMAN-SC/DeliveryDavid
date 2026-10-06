package com.proyecto.controller;

import com.proyecto.model.CategoriaProducto;
import com.proyecto.model.Negocio;
import com.proyecto.model.Producto;
import com.proyecto.model.Promocion;
import com.proyecto.model.TipoNegocio;
import com.proyecto.model.Usuario;
import com.proyecto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/panel")
public class PanelController {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private TipoNegocioRepository tipoNegocioRepo;
    @Autowired private NegocioRepository negocioRepo;
    @Autowired private CategoriaProductoRepository categoriaRepo;
    @Autowired private PromocionRepository promocionRepo;
    @Autowired private PedidoRepository pedidoRepo;
    @Autowired private ProductoRepository productoRepo;
    
    @Autowired private RolRepository rolRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    
    @Autowired private ZonaRepository zonaRepo;
    @Autowired private CentroComercialRepository centroRepo;
    
    @GetMapping
    public String mostrarPanel(
    		@RequestParam(name = "modulo", required = false) String modulo, 
    		@RequestParam(name = "q", required = false) String query,
    		Model model) {
    	
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (modulo == null || modulo.isEmpty()) {
            return esAdmin ? "redirect:/panel?modulo=usuarios" : "redirect:/panel?modulo=pedidos";
        }

        if (!esAdmin && !modulo.equals("pedidos")) {
            return "redirect:/panel?modulo=pedidos";
        }

        model.addAttribute("modulo", modulo);

        // Consultar la base de datos dinámicamente según el módulo seleccionado
        switch (modulo) {
            case "usuarios":
            	// 1. LÓGICA DEL BUSCADOR DE USUARIOS
                if (query != null && !query.isEmpty()) {
                    model.addAttribute("listaUsuarios", usuarioRepo.buscarPorFiltro(query));
                    model.addAttribute("q", query); // Mantiene el texto escrito en la barra
                } else {
                    model.addAttribute("listaUsuarios", usuarioRepo.findAll());
                }
                // Pasamos los roles a la vista para el Modal
                model.addAttribute("listaRoles", rolRepo.findAll());
                break;
            case "tipos_negocio":
                model.addAttribute("listaTipos", tipoNegocioRepo.findAll());
                break;
            case "negocios":
                model.addAttribute("listaNegocios", negocioRepo.findAll());
                // Listas para los combobox del Modal
                model.addAttribute("listaTipos", tipoNegocioRepo.findAll());
                model.addAttribute("listaZonas", zonaRepo.findAll());
                model.addAttribute("listaCentros", centroRepo.findAll());
                break;
            case "categorias":
            	model.addAttribute("listaCategorias", categoriaRepo.findAll());
                // Pasamos los negocios para que el administrador pueda asignar la categoría a uno de ellos
                model.addAttribute("listaNegocios", negocioRepo.findAll());
                break;
            case "productos": 
                model.addAttribute("listaProductos", productoRepo.findAll());
                // Pasamos las listas para los selectores del Modal
                model.addAttribute("listaNegocios", negocioRepo.findAll());
                model.addAttribute("listaCategorias", categoriaRepo.findAll());
                break;
            case "promociones":
            	model.addAttribute("listaPromociones", promocionRepo.findAll());
                model.addAttribute("listaNegocios", negocioRepo.findAll());
                break;
            case "pedidos":
                model.addAttribute("listaPedidos", pedidoRepo.findAll());
                break;
        }

        return "Panel";
    }
    
    // ==========================================
    // LÓGICA PARA CREAR Y EDITAR NEGOCIO
    // ==========================================
    @PostMapping("/negocios/guardar")
    public String guardarNegocio(@ModelAttribute Negocio negocio, 
                                 @RequestParam(value = "archivoLogo", required = false) MultipartFile archivoLogo, 
                                 RedirectAttributes redirectAttributes) {
        try {
            boolean esEdicion = negocio.getIdNegocio() != null;
            Negocio existente = esEdicion ? negocioRepo.findById(negocio.getIdNegocio()).orElse(null) : null;

            // 1. Manejo de la Imagen (Guardado Dinámico y Seguro)
            if (archivoLogo != null && !archivoLogo.isEmpty()) {
                
                // Extraemos la extensión original (ej. ".png")
                String nombreOriginal = archivoLogo.getOriginalFilename();
                String extension = (nombreOriginal != null && nombreOriginal.contains(".")) ? nombreOriginal.substring(nombreOriginal.lastIndexOf(".")) : "";
                
                // Generamos un código único para el archivo
                String nombreUnico = UUID.randomUUID().toString() + extension;
                
                // Apuntamos a la carpeta dinámica (se creará en la raíz de tu proyecto)
                Path directorio = Paths.get("RIDE_MEAL", "imagenes");
                
                // Si la carpeta no existe, Spring Boot la crea automáticamente
                if (!Files.exists(directorio)) {
                    Files.createDirectories(directorio);
                }
                
                // Guardamos el archivo físico
                Path rutaAbsoluta = directorio.resolve(nombreUnico);
                Files.write(rutaAbsoluta, archivoLogo.getBytes());
                
                // Guardamos solo el nombre (ej. "8f7d9...jpg") en la Base de Datos
                negocio.setImagenLogo(nombreUnico);
                
            } else if (existente != null) {
                // Si está editando pero no subió una foto nueva, conservamos la que ya tenía
                negocio.setImagenLogo(existente.getImagenLogo());
            }

            // 2. Manejo de campos nulos
            if (negocio.getZona() != null && negocio.getZona().getIdZona() == null) negocio.setZona(null);
            if (negocio.getCentroComercial() != null && negocio.getCentroComercial().getIdCentroComercial() == null) negocio.setCentroComercial(null);

            // 3. Guardar en BD
            negocioRepo.save(negocio);
            redirectAttributes.addFlashAttribute("mensajeExito", esEdicion ? "Negocio actualizado correctamente." : "Nuevo negocio creado con éxito.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ocurrió un error al procesar el archivo o guardar el negocio.");
        }
        return "redirect:/panel?modulo=negocios";
    }

    // ==========================================
    // LÓGICA PARA ELIMINAR NEGOCIO
    // ==========================================
    @PostMapping("/negocios/eliminar")
    public String eliminarNegocio(@RequestParam("idNegocio") Integer idNegocio, RedirectAttributes redirectAttributes) {
        try {
            negocioRepo.deleteById(idNegocio);
            redirectAttributes.addFlashAttribute("mensajeExito", "Negocio eliminado correctamente.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar porque este negocio tiene productos, promociones o pedidos activos.");
        }
        return "redirect:/panel?modulo=negocios";
    }
    
    // ==========================================
    // 2. LÓGICA PARA CREAR Y EDITAR USUARIO
    // ==========================================
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        
        boolean esEdicion = usuario.getIdUsuario() != null;

        // 1. Validar Correo Electrónico
        Usuario existenteCorreo = usuarioRepo.findByCorreoElectronico(usuario.getCorreoElectronico()).orElse(null);
        if (existenteCorreo != null && (!esEdicion || !existenteCorreo.getIdUsuario().equals(usuario.getIdUsuario()))) {
            redirectAttributes.addFlashAttribute("mensajeError", "El correo electrónico ya está registrado por otro usuario.");
            return "redirect:/panel?modulo=usuarios";
        }

        // 2. Validar DNI
        Usuario existenteDni = usuarioRepo.findByDni(usuario.getDni()).orElse(null);
        if (existenteDni != null && (!esEdicion || !existenteDni.getIdUsuario().equals(usuario.getIdUsuario()))) {
            redirectAttributes.addFlashAttribute("mensajeError", "El DNI ya está registrado en el sistema.");
            return "redirect:/panel?modulo=usuarios";
        }

        // 3. Validar Teléfono
        Usuario existenteTel = usuarioRepo.findByTelefono(usuario.getTelefono()).orElse(null);
        if (existenteTel != null && (!esEdicion || !existenteTel.getIdUsuario().equals(usuario.getIdUsuario()))) {
            redirectAttributes.addFlashAttribute("mensajeError", "El número de teléfono ya pertenece a otra cuenta.");
            return "redirect:/panel?modulo=usuarios";
        }

        // Si pasa todas las validaciones, procedemos a guardar
        if (esEdicion) {
            Usuario usuarioExistente = usuarioRepo.findById(usuario.getIdUsuario()).orElse(null);
            if (usuarioExistente != null) {
                usuarioExistente.setNombres(usuario.getNombres());
                usuarioExistente.setApellidos(usuario.getApellidos());
                usuarioExistente.setDni(usuario.getDni());
                usuarioExistente.setTelefono(usuario.getTelefono());
                usuarioExistente.setCorreoElectronico(usuario.getCorreoElectronico());
                usuarioExistente.setRol(usuario.getRol());
                
                // Si escribió una nueva contraseña, la encriptamos y la cambiamos
                if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                    usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
                }
                usuarioRepo.save(usuarioExistente);
                redirectAttributes.addFlashAttribute("mensajeExito", "Usuario actualizado correctamente.");
            }
        } else {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // Encriptamos
            usuarioRepo.save(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Nuevo usuario creado con éxito.");
        }
        
        return "redirect:/panel?modulo=usuarios";
    }

    // ==========================================
    // 3. LÓGICA PARA ELIMINAR USUARIO
    // ==========================================
    @PostMapping("/usuarios/eliminar")
    public String eliminarUsuario(@RequestParam("idUsuario") Integer idUsuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioRepo.deleteById(idUsuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario eliminado correctamente.");
        } catch (DataIntegrityViolationException e) {
            // Si MySQL rechaza la eliminación por llaves foráneas (el usuario tiene pedidos)
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar el usuario porque tiene pedidos registrados en el sistema.");
        }
        return "redirect:/panel?modulo=usuarios"; 
    }
 // ==========================================
    // LÓGICA PARA CREAR Y EDITAR CATEGORÍA
    // ==========================================
    @PostMapping("/categorias/guardar")
    public String guardarCategoria(@ModelAttribute CategoriaProducto categoria, 
                                   @RequestParam(value = "archivoLogo", required = false) MultipartFile archivoLogo, 
                                   RedirectAttributes redirectAttributes) {
        try {
            boolean esEdicion = categoria.getIdCategoriaProducto() != null;
            CategoriaProducto existente = esEdicion ? categoriaRepo.findById(categoria.getIdCategoriaProducto()).orElse(null) : null;

            // 1. Manejo de la Imagen (Carpeta 'categorias_comidas' según tu WebConfig)
            if (archivoLogo != null && !archivoLogo.isEmpty()) {
                String nombreOriginal = archivoLogo.getOriginalFilename();
                String extension = (nombreOriginal != null && nombreOriginal.contains(".")) ? nombreOriginal.substring(nombreOriginal.lastIndexOf(".")) : "";
                String nombreUnico = UUID.randomUUID().toString() + extension;
                
                // Usamos la carpeta dinámica especificada en tu WebConfig
                Path directorio = Paths.get("RIDE_MEAL", "categorias_comidas");
                if (!Files.exists(directorio)) {
                    Files.createDirectories(directorio);
                }
                
                Path rutaAbsoluta = directorio.resolve(nombreUnico);
                Files.write(rutaAbsoluta, archivoLogo.getBytes());
                
                categoria.setLogo(nombreUnico); // Guardamos el nombre generado en la BD
            } else if (existente != null) {
                categoria.setLogo(existente.getLogo()); // Mantiene la foto anterior si no sube una nueva
            }

            // 2. Guardar en BD
            categoriaRepo.save(categoria);
            redirectAttributes.addFlashAttribute("mensajeExito", esEdicion ? "Categoría actualizada correctamente." : "Nueva categoría creada con éxito.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ocurrió un error al procesar el archivo o guardar la categoría.");
        }
        return "redirect:/panel?modulo=categorias";
    }

    // ==========================================
    // LÓGICA PARA ELIMINAR CATEGORÍA
    // ==========================================
    @PostMapping("/categorias/eliminar")
    public String eliminarCategoria(@RequestParam("idCategoriaProducto") Integer idCategoriaProducto, RedirectAttributes redirectAttributes) {
        try {
            categoriaRepo.deleteById(idCategoriaProducto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Categoría eliminada correctamente.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar esta categoría porque ya tiene productos asignados.");
        }
        return "redirect:/panel?modulo=categorias";
    }
 // ==========================================
    // LÓGICA PARA CREAR Y EDITAR PRODUCTO
    // ==========================================
    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, 
                                  @RequestParam(value = "archivoImagen", required = false) MultipartFile archivoImagen, 
                                  RedirectAttributes redirectAttributes) {
        try {
            boolean esEdicion = producto.getIdProducto() != null;
            Producto existente = esEdicion ? productoRepo.findById(producto.getIdProducto()).orElse(null) : null;

            // 1. Manejo de la Imagen (Carpeta 'productos' según tu WebConfig)
            if (archivoImagen != null && !archivoImagen.isEmpty()) {
                String nombreOriginal = archivoImagen.getOriginalFilename();
                String extension = (nombreOriginal != null && nombreOriginal.contains(".")) ? nombreOriginal.substring(nombreOriginal.lastIndexOf(".")) : "";
                String nombreUnico = UUID.randomUUID().toString() + extension;
                
                Path directorio = Paths.get("RIDE_MEAL", "productos");
                if (!Files.exists(directorio)) {
                    Files.createDirectories(directorio);
                }
                
                Path rutaAbsoluta = directorio.resolve(nombreUnico);
                Files.write(rutaAbsoluta, archivoImagen.getBytes());
                
                producto.setImagen(nombreUnico);
            } else if (existente != null) {
                producto.setImagen(existente.getImagen()); // Mantiene la foto anterior
            }

            // 2. Guardar en BD
            productoRepo.save(producto);
            redirectAttributes.addFlashAttribute("mensajeExito", esEdicion ? "Producto actualizado correctamente." : "Nuevo producto creado con éxito.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ocurrió un error al procesar el archivo o guardar el producto.");
        }
        return "redirect:/panel?modulo=productos";
    }

    // ==========================================
    // LÓGICA PARA ELIMINAR PRODUCTO
    // ==========================================
    @PostMapping("/productos/eliminar")
    public String eliminarProducto(@RequestParam("idProducto") Integer idProducto, RedirectAttributes redirectAttributes) {
        try {
            productoRepo.deleteById(idProducto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto eliminado correctamente.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar el producto porque está incluido en pedidos, carritos o promociones.");
        }
        return "redirect:/panel?modulo=productos";
    }
 // En los @PostMapping (Al final de la clase):
    @PostMapping("/promociones/guardar")
    public String guardarPromocion(@ModelAttribute Promocion promo, 
                                   @RequestParam(value = "dias", required = false) List<String> dias,
                                   @RequestParam(value = "archivoBanner", required = false) MultipartFile archivoBanner, 
                                   RedirectAttributes redirectAttributes) {
        try {
            boolean esEdicion = promo.getIdPromocion() != null;
            Promocion existente = esEdicion ? promocionRepo.findById(promo.getIdPromocion()).orElse(null) : null;

            // Procesar los checkboxes de días hacia un String "1,2,3"
            if (dias != null && !dias.isEmpty()) {
                promo.setDiasActivos(String.join(",", dias));
            } else {
                promo.setDiasActivos("");
            }

            // Manejo de la Imagen (Carpeta dinámica 'banner')
            if (archivoBanner != null && !archivoBanner.isEmpty()) {
                String nombreOriginal = archivoBanner.getOriginalFilename();
                String extension = (nombreOriginal != null && nombreOriginal.contains(".")) ? nombreOriginal.substring(nombreOriginal.lastIndexOf(".")) : "";
                String nombreUnico = UUID.randomUUID().toString() + extension;
                
                Path directorio = Paths.get("RIDE_MEAL", "promociones");
                if (!Files.exists(directorio)) Files.createDirectories(directorio);
                Files.write(directorio.resolve(nombreUnico), archivoBanner.getBytes());
                
                promo.setImagenBanner(nombreUnico);
            } else if (existente != null) {
                promo.setImagenBanner(existente.getImagenBanner());
            }
            
         // INTERCEPCIÓN DEL CHECKBOX: Si viene nulo (desmarcado), lo forzamos a false
            if (promo.getMostrarEnIndex() == null) {
                promo.setMostrarEnIndex(false);
            }
            
            promocionRepo.save(promo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Promoción guardada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar la promoción.");
        }
        return "redirect:/panel?modulo=promociones";
    }
    
    @PostMapping("/promociones/eliminar")
    public String eliminarPromocion(@RequestParam("idPromocion") Integer idPromocion, RedirectAttributes redirectAttributes) {
        promocionRepo.deleteById(idPromocion);
        redirectAttributes.addFlashAttribute("mensajeExito", "Promoción eliminada.");
        return "redirect:/panel?modulo=promociones";
    }
    
    @PostMapping("/tipos_negocio/ocultar")
    public String ocultarTipoNegocio(@RequestParam("idTipoNegocio") Integer idTipoNegocio, RedirectAttributes redirectAttributes) {
        TipoNegocio tipo = tipoNegocioRepo.findById(idTipoNegocio).orElse(null);
        if (tipo != null) {
            tipo.setEstado(false); // Lo marcamos como oculto
            tipoNegocioRepo.save(tipo);
            redirectAttributes.addFlashAttribute("mensajeExito", "El tipo de negocio ha sido ocultado del catálogo.");
        }
        return "redirect:/panel?modulo=tipos_negocio";
    }

    @PostMapping("/tipos_negocio/guardar")
    public String guardarTipoNegocio(@ModelAttribute TipoNegocio tipo, RedirectAttributes redirectAttributes) {
        try {
            // Si es un tipo nuevo (ID nulo), por defecto lo hacemos visible
            if (tipo.getIdTipoNegocio() == null && tipo.getEstado() == null) {
                tipo.setEstado(true);
            }
            
            tipoNegocioRepo.save(tipo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Tipo de negocio guardado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ocurrió un error al guardar el tipo de negocio.");
        }
        return "redirect:/panel?modulo=tipos_negocio";
    }
}
