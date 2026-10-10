package com.proyecto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.model.DireccionUsuario;

@Repository
public interface DireccionUsuarioRepository extends JpaRepository<DireccionUsuario, Integer> {


    // Todas las direcciones del usuario.
    // Principal aparece primero.
    List<DireccionUsuario> findByUsuarioIdUsuarioOrderByPrincipalDescIdDireccionUsuarioAsc(Integer idUsuario);


    // Muy importante:
    // comprueba que la dirección realmente
    // pertenece al usuario.
    Optional<DireccionUsuario> findByIdDireccionUsuarioAndUsuarioIdUsuario(Integer idDireccion,Integer idUsuario);
    
    boolean existsByUsuarioIdUsuarioAndPrincipalTrue(
            Integer idUsuario
    );
    
}
