package com.proyecto.repository;

import com.proyecto.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

  Optional<Usuario> findByTelefono(String telefono);

  Optional<Usuario> findByCorreoElectronico(String correoElectronico);

  Optional<Usuario> findByDni(String dni);

  @Query("SELECT u FROM Usuario u WHERE u.nombres LIKE %:filtro% OR u.dni LIKE %:filtro%")
  List<Usuario> buscarPorFiltro(@Param("filtro") String filtro);

  Optional<Usuario> findByTokenPassword(String tokenPassword);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("select u from Usuario u where u.tokenPassword = :hash")
  Optional<Usuario> findForReset(@Param("hash") String hash);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("select u from Usuario u where u.correoElectronico = :email")
  Optional<Usuario> findForUpdateByEmail(@Param("email") String email);
}
