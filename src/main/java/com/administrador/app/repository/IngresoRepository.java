package com.administrador.app.repository;

import com.administrador.app.domain.Ingreso;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IngresoRepository extends JpaRepository<Ingreso, Long>{
    
    public List<Ingreso> findAllByIsPeriodico(boolean periodico);

    public Optional<Ingreso> findByFechaBetweenAndCvePeriodicoAndIsPeriodico(Date fechaInicio, Date fechaFin, String cve, boolean isPeriodico);
}
