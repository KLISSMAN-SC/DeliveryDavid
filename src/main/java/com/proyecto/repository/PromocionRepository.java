package com.proyecto.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.model.Promocion;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion,Integer>{

}
