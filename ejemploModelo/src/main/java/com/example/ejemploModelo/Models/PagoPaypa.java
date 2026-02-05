package com.example.ejemploModelo.Models;
import jakarta.persistence.*;

@Entity
@Table(name = "pago_paypal")
@DiscriminatorValue("PAYPAL")
public class PagoPaypa extends Pago {
    private String emailCuenta;

    public String getEmailCuenta() { return emailCuenta; }
    public void setEmailCuenta(String emailCuenta) { this.emailCuenta = emailCuenta; }
    
    @Override
    public String getTipoPago() { return "PAYPAL"; }
}
