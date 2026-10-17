package com.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.model.ConsentimientoLegal;
@Repository
public interface ConsentimientoLegalRepository extends JpaRepository<ConsentimientoLegal,Integer>  {

}
