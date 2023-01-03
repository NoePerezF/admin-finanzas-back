package com.administrador.app.repository;

import com.administrador.app.domain.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TarjetaRepository extends JpaRepository<Tarjeta, Long>{
    
}
