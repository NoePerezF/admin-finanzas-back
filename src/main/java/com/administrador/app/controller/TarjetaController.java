package com.administrador.app.controller;

import com.administrador.app.domain.Gasto;
import com.administrador.app.domain.Ingreso;
import com.administrador.app.domain.Tarjeta;
import com.administrador.app.repository.GastoRepository;
import com.administrador.app.repository.IngresoRepository;
import com.administrador.app.repository.TarjetaRepository;
import com.administrador.app.util.MensajeError;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/tarjeta", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class TarjetaController {
    
    @Autowired
    private TarjetaRepository tarjetaRepository;
    
    @Autowired
    private GastoRepository gastoRepository;
    
    @Autowired
    private IngresoRepository ingresoRepository;
    
    @PostMapping(value = "/nueva")
    public ResponseEntity<?> nuevaTarjeta(@RequestBody Tarjeta tarjeta){
        try {
            tarjeta = tarjetaRepository.save(tarjeta);
            MensajeError mensaje = new MensajeError("Tarjeta creada con exito",false);
            return new ResponseEntity<>(mensaje,HttpStatus.OK);
        } catch (Exception e) {
            MensajeError mensaje = new MensajeError(e.getMessage(),true);
            return new ResponseEntity<>(mensaje,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(value = "/tarjetas")
    public ResponseEntity<?> getTarjetas() {
        try {
            List<Tarjeta> tarjetas =  tarjetaRepository.findAll();
            for(Tarjeta t : tarjetas) {
                Double ingresos = 0.0;
                Double gastos = 0.0;
                for(Ingreso i : t.getIngresos()) {
                    if(!i.isPeriodico())
                        ingresos += i.getMonto();
                }
                for(Gasto g : t.getGastos()) {
                    if(!g.isPeriodico())
                        gastos += g.getMonto();
                }
                if(t.isDebito()){
                    t.setSaldo(ingresos-gastos);
                }else{
                    t.setSaldo(gastos-ingresos);
                }
                
            }
            return new ResponseEntity<>(tarjetas, HttpStatus.OK);
        } catch (Exception e) {
            MensajeError mensaje = new MensajeError(e.getMessage(),true);
            return new ResponseEntity<>(mensaje,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = "/add-gasto")
    public ResponseEntity<?> addGasto(@RequestBody Tarjeta tarjeta) {
        try {
            Gasto gasto = tarjeta.getGastos().get(0);
            Optional<Tarjeta> opTarjeta = tarjetaRepository.findById(tarjeta.getId());
            if(opTarjeta.isEmpty()) {
                return new ResponseEntity<>(new MensajeError("No existe la tarjeta",true),HttpStatus.BAD_REQUEST);
            }
            tarjeta = opTarjeta.get();
            gasto.setTarjeta(tarjeta);
            System.err.println(gasto.isPeriodico());
            LocalDate myLocalDate = LocalDate.now();
            gasto.setFecha(Date.from(myLocalDate.atStartOfDay(ZoneId.of("GMT-6")).toInstant()));
            System.err.println(gasto.getFechaFin());
           
            Gasto gastoNuevo = gastoRepository.save(gasto);
            
            
            if(gasto.isPeriodico()){
                gastoNuevo.setCvePeriodico(gasto.getId()+gasto.getNombre());
                gastoRepository.save(gastoNuevo);
            }
            return new ResponseEntity<>(new MensajeError("Gasto creado con exito",false),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MensajeError(e.getMessage(),true),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = "/add-ingreso")
    public ResponseEntity<?> addIngreso(@RequestBody Tarjeta tarjeta) {
        try {
            Ingreso ingreso = tarjeta.getIngresos().get(0);
            System.err.println(ingreso.isPeriodico());
            Optional<Tarjeta> opTarjeta = tarjetaRepository.findById(tarjeta.getId());
            if(opTarjeta.isEmpty()) {
                return new ResponseEntity<>(new MensajeError("No existe la tarjeta",true),HttpStatus.BAD_REQUEST);
            }
            tarjeta = opTarjeta.get();
            ingreso.setTarjeta(tarjeta);
            LocalDate myLocalDate = LocalDate.now();
            ingreso.setFecha(Date.from(myLocalDate.atStartOfDay(ZoneId.of("GMT-6")).toInstant()));
            Ingreso ingresoNuevo = ingresoRepository.save(ingreso);
            if(ingreso.isPeriodico()) {
                ingresoNuevo.setCvePeriodico(ingreso.getId()+ingreso.getNombre());
                ingresoRepository.save(ingresoNuevo);
            }
            return new ResponseEntity<>(new MensajeError("Ingreso creado con exito",false),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MensajeError(e.getMessage(),true),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(value = "/actualizar")
    public ResponseEntity<?> actualuizar() {
        try {
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
            LocalDate myLocalDate = LocalDate.now();
            int dia = myLocalDate.getDayOfMonth();
            List<Gasto> gastos = gastoRepository.findAllByIsPeriodico(true);
            List<Ingreso> ingresos = ingresoRepository.findAllByIsPeriodico(true);
            System.err.println(ingresos.size());
            for(Gasto g : gastos) {
                if(dia >= g.getDiaPeriodico()){
                   Optional<Gasto> opGasto = gastoRepository.findByFechaBetweenAndCvePeriodicoAndIsPeriodico(Date.from(myLocalDate.withDayOfMonth(1).atStartOfDay(ZoneId.of("GMT-6")).toInstant()), Date.from(myLocalDate.atStartOfDay(ZoneId.of("GMT-6")).toInstant()), g.getCvePeriodico(),false);
                   if(opGasto.isEmpty()){
                       Gasto gasto = new Gasto(null,g.getNombre(),g.getMonto(), Date.from(myLocalDate.atStartOfDay(ZoneId.of("GMT-6")).toInstant()),false,g.getCvePeriodico(),g.getDiaPeriodico(),g.getTarjeta(),g.getFechaFin());
                       gastoRepository.save(gasto);
                   }
                }
            }
            for(Ingreso i : ingresos) {
                if(dia >= i.getDiaPeriodico()){
                Optional<Ingreso> opIngreso = ingresoRepository.findByFechaBetweenAndCvePeriodicoAndIsPeriodico(Date.from(myLocalDate.withDayOfMonth(1).atStartOfDay(ZoneId.of("GMT-6")).toInstant()), Date.from(myLocalDate.atStartOfDay(ZoneId.of("GMT-6")).toInstant()), i.getCvePeriodico(),false);
                if(opIngreso.isEmpty()) {
                    Ingreso ingreso = new Ingreso(null,i.getNombre(),i.getMonto(),Date.from(myLocalDate.atStartOfDay(ZoneId.of("GMT-6")).toInstant()),false,i.getCvePeriodico(),i.getDiaPeriodico(),i.getTarjeta());
                    ingresoRepository.save(ingreso);
                }
                }
            }
            return new ResponseEntity<>(new MensajeError("Actualizado con exito",false), HttpStatus.OK);
        } catch (Exception e) {
            MensajeError mensaje = new MensajeError(e.getMessage(),true);
            return new ResponseEntity<>(mensaje,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
