package com.proyecto.repository;

import com.proyecto.model.Carrito;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

  Optional<Carrito> findByUsuarioIdUsuarioAndNegocioIdNegocioAndEstado(
      Integer idUsuario, Integer idNegocio, String estado);

  @Query(
      """
          SELECT DISTINCT c
          FROM Carrito c

          LEFT JOIN FETCH c.detalles d
          LEFT JOIN FETCH d.producto

          LEFT JOIN FETCH c.negocio n
          LEFT JOIN FETCH n.zona

          WHERE c.usuario.idUsuario = :idUsuario
          AND c.negocio.idNegocio = :idNegocio
          AND c.estado = 'ACTIVO'
      """)
  Optional<Carrito> buscarCarritoCompleto(
      @Param("idUsuario") Integer idUsuario, @Param("idNegocio") Integer idNegocio);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query(
      "select c from Carrito c where c.usuario.idUsuario = :usuario and c.negocio.idNegocio ="
          + " :negocio and c.estado = 'ACTIVO'")
  Optional<Carrito> findActiveForUpdate(
      @Param("usuario") Integer usuario, @Param("negocio") Integer negocio);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query(
      "select c from Carrito c where c.idCarrito=:cart and c.usuario.idUsuario=:usuario and"
          + " c.negocio.idNegocio=:negocio")
  Optional<Carrito> findCartForUpdate(
      @Param("cart") Integer cart,
      @Param("usuario") Integer usuario,
      @Param("negocio") Integer negocio);
}
