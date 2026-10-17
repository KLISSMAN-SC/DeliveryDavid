package com.proyecto.services;

import com.proyecto.model.Producto;
import com.proyecto.repository.ProductoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

  @Autowired private ProductoRepository productoRepository;

  public List<Producto> listarProductosPorNegocio(Integer idNegocio) {

    return productoRepository.findByNegocioIdNegocioAndDisponibleTrue(idNegocio);
  }

  public List<Producto> buscarProductosPorNegocio(Integer idNegocio, String texto) {

    // Si el buscador está vacío, mostrar todos
    if (texto == null || texto.trim().isEmpty()) {

      return productoRepository.findByNegocioIdNegocioAndDisponibleTrue(idNegocio);
    }

    return productoRepository.buscarProductosPorNegocio(idNegocio, texto.trim());
  }
  public Page<Producto>
  listarAdminPorNegocio(
          Integer idNegocio,
          String busqueda,
          int pagina,
          int cantidad) {


      Pageable pageable =
          PageRequest.of(
              pagina,
              cantidad,
              Sort.by(
                  "idProducto"
              ).descending()
          );


      if (
          busqueda != null &&
          !busqueda.isBlank()
      ) {

          return productoRepository
              .findByNegocio_IdNegocioAndNombreContainingIgnoreCase(
                  idNegocio,
                  busqueda.trim(),
                  pageable
              );
      }


      return productoRepository
          .findByNegocio_IdNegocio(
              idNegocio,
              pageable
          );
  }
}
