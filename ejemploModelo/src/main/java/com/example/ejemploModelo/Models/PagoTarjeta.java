package com.example.ejemploModelo.Models;
import jakarta.persistence.*;

@Entity
@Table(name = "pago_tarjeta")
@DiscriminatorValue("TARJETA")
public class PagoTarjeta extends Pago {
    private String numeroTarjeta;
    private String nombreTitular;
    private String fechaExpiracion;
    private String cvv;

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }
    public String getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(String fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    @Override
    public String getTipoPago() { return "TARJETA"; }
}
