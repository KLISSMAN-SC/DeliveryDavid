package com.proyecto.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.model.Carrito;
import com.proyecto.model.DetalleCarrito;
import com.proyecto.model.Negocio;
import com.proyecto.model.Producto;
import com.proyecto.model.Usuario;
import com.proyecto.repository.CarritoRepository;
import com.proyecto.repository.DetalleCarritoRepository;
import com.proyecto.repository.NegocioRepository;
import com.proyecto.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
public class CarritoService {
	 
	@Autowired
	 private CarritoRepository carritoRepository;

	 @Autowired
	 private DetalleCarritoRepository detalleCarritoRepository;

	 @Autowired
	 private ProductoRepository productoRepository;

	 @Autowired
	 private NegocioRepository negocioRepository;
	 
	 public Carrito obtenerOCrearCarrito(
	            Integer idUsuario,
	            Integer idNegocio) {

	        return carritoRepository
	                .findByUsuarioIdUsuarioAndNegocioIdNegocioAndEstado(
	                        idUsuario,
	                        idNegocio,
	                        "ACTIVO"
	                )
	                .orElseGet(() -> {

	                    Usuario usuario = new Usuario();
	                    usuario.setIdUsuario(idUsuario);

	                    Negocio negocio = negocioRepository
	                            .findById(idNegocio)
	                            .orElseThrow(() ->
	                                new RuntimeException(
	                                    "No existe el negocio"
	                                )
	                            );

	                    Carrito carrito = new Carrito();

	                    carrito.setUsuario(usuario);
	                    carrito.setNegocio(negocio);
	                    carrito.setEstado("ACTIVO");
	                    carrito.setFechaCreacion(LocalDateTime.now());
	                    carrito.setFechaActualizacion(LocalDateTime.now());

	                    return carritoRepository.save(carrito);
	                });
	    }


	    // =====================================================
	    // AGREGAR PRODUCTO
	    // =====================================================
	 @Transactional
	 public Carrito agregarProducto(
	         Integer idUsuario,
	         Integer idNegocio,
	         Integer idProducto) {


	     Carrito carrito =
	             obtenerOCrearCarrito(
	                     idUsuario,
	                     idNegocio
	             );


	     Producto producto =
	             productoRepository
	             .findById(idProducto)
	             .orElseThrow(() ->
	                 new RuntimeException(
	                     "Producto no encontrado"
	                 )
	             );


	     if (!producto
	             .getNegocio()
	             .getIdNegocio()
	             .equals(idNegocio)) {

	         throw new RuntimeException(
	             "El producto no pertenece al negocio"
	         );
	     }


	     detalleCarritoRepository
	             .agregarOIncrementar(
	                     carrito.getIdCarrito(),
	                     producto.getIdProducto(),
	                     producto.getPrecio()
	             );


	     // MUY IMPORTANTE:
	     // volver a consultar el carrito actualizado

	     return carritoRepository
	             .buscarCarritoCompleto(
	                     idUsuario,
	                     idNegocio
	             )
	             .orElseThrow(() ->
	                 new RuntimeException(
	                     "No se pudo recuperar el carrito"
	                 )
	             );
	 }
	 
	 public Carrito disminuirProducto(
		        Integer idUsuario,
		        Integer idNegocio,
		        Integer idProducto) {

		    // 1. Buscar el carrito activo
		    Carrito carrito = carritoRepository
		            .findByUsuarioIdUsuarioAndNegocioIdNegocioAndEstado(
		                    idUsuario,
		                    idNegocio,
		                    "ACTIVO"
		            )
		            .orElseThrow(() ->
		                    new RuntimeException("No existe carrito activo")
		            );


		    // 2. Buscar el producto dentro del carrito
		    DetalleCarrito detalle = detalleCarritoRepository
		            .findByCarritoIdCarritoAndProductoIdProducto(
		                    carrito.getIdCarrito(),
		                    idProducto
		            )
		            .orElseThrow(() ->
		                    new RuntimeException(
		                            "El producto no existe en el carrito"
		                    )
		            );


		    // 3. Si hay más de uno, restamos
		    if (detalle.getCantidad() > 1) {

		        detalle.setCantidad(
		                detalle.getCantidad() - 1
		        );

		        detalleCarritoRepository.save(detalle);

		    } else {

		        // Si cantidad = 1, se elimina completamente
		        detalleCarritoRepository.delete(detalle);

		        // También lo quitamos de la lista que está en memoria
		        carrito.getDetalles().removeIf(
		                d -> d.getIdDetalleCarrito()
		                        .equals(detalle.getIdDetalleCarrito())
		        );
		    }


		    carrito.setFechaActualizacion(
		            LocalDateTime.now()
		    );

		    carritoRepository.save(carrito);


		    return carrito;
		}
	 public Carrito eliminarProducto(
		        Integer idUsuario,
		        Integer idNegocio,
		        Integer idProducto) {

		    // Buscar carrito activo
		    Carrito carrito = carritoRepository
		            .findByUsuarioIdUsuarioAndNegocioIdNegocioAndEstado(
		                    idUsuario,
		                    idNegocio,
		                    "ACTIVO"
		            )
		            .orElseThrow(() ->
		                    new RuntimeException("No existe carrito activo")
		            );


		    // Buscar detalle
		    DetalleCarrito detalle = detalleCarritoRepository
		            .findByCarritoIdCarritoAndProductoIdProducto(
		                    carrito.getIdCarrito(),
		                    idProducto
		            )
		            .orElseThrow(() ->
		                    new RuntimeException(
		                            "El producto no existe en el carrito"
		                    )
		            );


		    // Eliminar completamente
		    detalleCarritoRepository.delete(detalle);


		    // Actualizar carrito
		    carrito.setFechaActualizacion(
		            LocalDateTime.now()
		    );

		    carritoRepository.save(carrito);


		    // Volver a consultar para devolverlo actualizado
		    return obtenerCarritoActual(
		            idUsuario,
		            idNegocio
		    );
		}


	    // =====================================================
	    // OBTENER CARRITO ACTUAL
	    // =====================================================

	    public Carrito obtenerCarritoActual(
	            Integer idUsuario,
	            Integer idNegocio) {

	        return carritoRepository
	                .findByUsuarioIdUsuarioAndNegocioIdNegocioAndEstado(
	                        idUsuario,
	                        idNegocio,
	                        "ACTIVO"
	                )
	                .orElse(null);
	    }
}
