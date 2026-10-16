package com.proyecto.services;

import com.proyecto.model.DireccionUsuario;
import com.proyecto.model.Usuario;
import com.proyecto.repository.DireccionUsuarioRepository;
import com.proyecto.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DireccionUsuarioService {

  @Autowired private DireccionUsuarioRepository direccionUsuarioRepository;

  @Autowired private UsuarioRepository usuarioRepository;

  public DireccionUsuario guardarDireccion(
      Integer idUsuario,
      String alias,
      String direccion,
      String referencia,
      BigDecimal latitud,
      BigDecimal longitud) {
    if (alias == null || alias.isBlank()) {

      throw new IllegalArgumentException("El nombre de la dirección es obligatorio");
    }

    if (direccion == null || direccion.isBlank()) {

      throw new IllegalArgumentException("La dirección es obligatoria");
    }

    if (latitud == null || longitud == null) {

      throw new IllegalArgumentException("La ubicación es obligatoria");
    }

    if (alias.length() > 30) {

      throw new IllegalArgumentException("El alias no puede superar 30 caracteres");
    }

    if (direccion.length() > 150) {

      throw new IllegalArgumentException("La dirección no puede superar 150 caracteres");
    }

    com.proyecto.security.InputChecks.coordinates(latitud, longitud);
    com.proyecto.security.InputChecks.optional(referencia, 100);
    if (direccion.length() > 100)
      throw com.proyecto.security.BusinessException.invalid(
          "La dirección no puede superar 100 caracteres.");
    Usuario usuario =
        usuarioRepository
            .findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    DireccionUsuario nueva = new DireccionUsuario();

    nueva.setUsuario(usuario);

    nueva.setAlias(alias);

    nueva.setDireccion(direccion);

    nueva.setReferencia(referencia != null ? referencia.trim() : null);

    nueva.setLatitud(latitud);

    nueva.setLongitud(longitud);

    // Si es la primera dirección,
    // automáticamente será principal
    boolean yaTienePrincipal =
        direccionUsuarioRepository.existsByUsuarioIdUsuarioAndPrincipalTrue(idUsuario);

    nueva.setPrincipal(!yaTienePrincipal);

    return direccionUsuarioRepository.save(nueva);
  }

  public List<DireccionUsuario> listarPorUsuario(Integer idUsuario) {

    return direccionUsuarioRepository
        .findByUsuarioIdUsuarioOrderByPrincipalDescIdDireccionUsuarioAsc(idUsuario);
  }

  public DireccionUsuario obtenerDireccionUsuario(Integer idDireccionUsuario, Integer idUsuario) {

    return direccionUsuarioRepository
        .findByIdDireccionUsuarioAndUsuarioIdUsuario(idDireccionUsuario, idUsuario)
        .orElseThrow(() -> com.proyecto.security.BusinessException.missing());
  }

  public DireccionUsuario obtenerPrincipalOPrimera(Integer idUsuario) {

    List<DireccionUsuario> direcciones = listarPorUsuario(idUsuario);

    if (direcciones.isEmpty()) {
      return null;
    }

    // Como el Repository ordena
    // principal DESC, la primera
    // será la principal.
    return direcciones.get(0);
  }

  // =====================================
  // ELIMINAR DIRECCIÓN
  // =====================================

  @Transactional
  public void eliminarDireccion(Integer idDireccionUsuario, Integer idUsuario) {

    // Verificamos que pertenezca
    // realmente al usuario.

    DireccionUsuario direccion = obtenerDireccionUsuario(idDireccionUsuario, idUsuario);

    boolean eraPrincipal = Boolean.TRUE.equals(direccion.getPrincipal());

    direccionUsuarioRepository.delete(direccion);

    direccionUsuarioRepository.flush();

    // Si eliminó la principal,
    // elegimos otra.

    if (eraPrincipal) {

      List<DireccionUsuario> restantes = listarPorUsuario(idUsuario);

      if (!restantes.isEmpty()) {

        DireccionUsuario nuevaPrincipal = restantes.get(0);

        nuevaPrincipal.setPrincipal(true);

        direccionUsuarioRepository.save(nuevaPrincipal);
      }
    }
  }

  public DireccionUsuario obtenerPorIdYUsuario(Integer idDireccionUsuario, Integer idUsuario) {

    return direccionUsuarioRepository
        .findByIdDireccionUsuarioAndUsuarioIdUsuario(idDireccionUsuario, idUsuario)
        .orElseThrow(() -> com.proyecto.security.BusinessException.missing());
  }
}
