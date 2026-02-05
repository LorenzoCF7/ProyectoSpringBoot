package com.example.ejemploModelo.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;
    private Double precio;
    private int duracionMeses;
    private String descripcion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecio() { return precio; }   
    public void setPrecio(Double precio) { this.precio = precio; }
    public int getDuracionMeses() { return duracionMeses; }
    public void setDuracionMeses(int duracionMeses) { this.duracionMeses = duracionMeses; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
}
