package com.proyecto.repository;

import com.proyecto.model.DetallePedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

  List<DetallePedido> findByPedidoIdPedido(Integer idPedido);

  List<DetallePedido> findByPedidoIdPedidoIn(List<Integer> idsPedidos);
}
