package com.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
	Optional<Usuario> findByTelefono(String telefono);
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
    Optional<Usuario> findByDni(String dni);
    
    @Query("SELECT u FROM Usuario u WHERE u.nombres LIKE %:filtro% OR u.dni LIKE %:filtro%")
    List<Usuario> buscarPorFiltro(@Param("filtro") String filtro);
    
}