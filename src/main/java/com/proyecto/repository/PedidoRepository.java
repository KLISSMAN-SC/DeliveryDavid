package com.proyecto.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Integer>{
	
	List<Pedido>
    findByUsuarioIdUsuarioOrderByIdPedidoDesc(
        Integer idUsuario
    );
}
