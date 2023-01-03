package com.administrador.app.repository;

import com.administrador.app.domain.Gasto;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GastoRepository extends JpaRepository<Gasto, Long>{
    
    public List<Gasto> findAllByIsPeriodico(boolean periodico);
    
    public Optional<Gasto> findByFechaBetweenAndCvePeriodicoAndIsPeriodico(Date fechaInicio, Date fechaFin, String cve, boolean isPeriodico);
}
