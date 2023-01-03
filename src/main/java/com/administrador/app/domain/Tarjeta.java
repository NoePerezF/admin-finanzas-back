package com.administrador.app.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Tarjeta implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String nombre;
    
    @Column
    private String numero;
    
    @Column
    private int diaCorte;
    
    @Column
    private Double saldo;
    
    @Column
    @JsonProperty("isDebito")
    private boolean isDebito;
    
    @OneToMany(mappedBy = "tarjeta")
    private List<Gasto> gastos;
    
    @OneToMany(mappedBy = "tarjeta")
    private List<Ingreso> ingresos;
    
}
