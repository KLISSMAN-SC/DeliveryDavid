package com.proyecto.repository;

import com.proyecto.model.Configuracion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer> {

  Optional<Configuracion> findFirstByOrderByIdConfiguracionAsc();
}
