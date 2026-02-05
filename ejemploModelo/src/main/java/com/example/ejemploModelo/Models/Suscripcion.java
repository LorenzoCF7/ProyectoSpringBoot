package com.example.ejemploModelo.Models;
import java.time.LocalDate;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Audited
@Entity
public class Suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private EstadoSuscripcion estado;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    @ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = true)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Usuario usuario;
    @ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = true)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Plan plan;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EstadoSuscripcion getEstado() { return estado; }
    public void setEstado(EstadoSuscripcion estado) { this.estado = estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
}