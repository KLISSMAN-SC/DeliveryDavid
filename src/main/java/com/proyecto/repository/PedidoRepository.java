package com.proyecto.repository;

import com.proyecto.model.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

  List<Pedido> findByUsuarioIdUsuarioOrderByIdPedidoDesc(Integer idUsuario);

  // PEDIDO ESPECÍFICO DE ESE USUARIO
  Optional<Pedido> findByIdPedidoAndUsuarioIdUsuario(Integer idPedido, Integer idUsuario);

  Optional<Pedido> findByIdCarritoOrigen(Integer idCarritoOrigen);

  List<Pedido> findByRepartidorIdUsuarioOrderByIdPedidoDesc(Integer idUsuario);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query(
      "select p from Pedido p where p.idPedido=:pedido and p.usuario.idUsuario=:usuario")
  Optional<Pedido> findForCancel(
      @org.springframework.data.repository.query.Param("pedido") Integer pedido,
      @org.springframework.data.repository.query.Param("usuario") Integer usuario);
}
