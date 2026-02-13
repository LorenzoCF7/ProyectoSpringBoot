package com.example.ejemploModelo.Models;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;

@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo")
public abstract class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double monto;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPago;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "factura_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Factura factura;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }
    
    // Método abstracto para obtener el tipo de pago de forma segura
    public abstract String getTipoPago();
}
