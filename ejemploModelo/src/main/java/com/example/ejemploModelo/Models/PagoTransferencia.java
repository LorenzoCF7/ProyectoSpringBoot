package com.example.ejemploModelo.Models;
import jakarta.persistence.*;

@Entity
@Table(name = "pago_transferencia")
@DiscriminatorValue("TRANSFERENCIA")
public class PagoTransferencia extends Pago {

    private String bancoOrigen;
    private String bancoDestino;
    private String numeroCuentaOrigen;
    private String numeroCuentaDestino;
    private String codigoTransferencia;

    public String getBancoOrigen() { return bancoOrigen; }
    public void setBancoOrigen(String bancoOrigen) { this.bancoOrigen = bancoOrigen; }
    public String getBancoDestino() { return bancoDestino; }
    public void setBancoDestino(String bancoDestino) { this.bancoDestino = bancoDestino; }
    public String getNumeroCuentaOrigen() { return numeroCuentaOrigen; }
    public void setNumeroCuentaOrigen(String numeroCuentaOrigen) { this.numeroCuentaOrigen = numeroCuentaOrigen; }
    public String getNumeroCuentaDestino() { return numeroCuentaDestino; }
    public void setNumeroCuentaDestino(String numeroCuentaDestino) { this.numeroCuentaDestino = numeroCuentaDestino; }
    public String getCodigoTransferencia() { return codigoTransferencia; }
    public void setCodigoTransferencia(String codigoTransferencia) { this.codigoTransferencia = codigoTransferencia; }
    
    @Override
    public String getTipoPago() { return "TRANSFERENCIA"; }
}
