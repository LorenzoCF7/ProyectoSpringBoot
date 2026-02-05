package com.example.ejemploModelo.Models;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;


@Entity
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double monto;
    private String descripcion;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;
    private boolean pagada;
    @OneToOne(fetch = jakarta.persistence.FetchType.EAGER)
    @JoinColumn(name = "suscripcion_id", nullable = false)
    private Suscripcion suscripcion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public boolean isPagada() { return pagada; }
    public void setPagada(boolean pagada) { this.pagada = pagada; }
    public Suscripcion getSuscripcion() { return suscripcion; }
    public void setSuscripcion(Suscripcion suscripcion) { this.suscripcion = suscripcion; }
    
}
