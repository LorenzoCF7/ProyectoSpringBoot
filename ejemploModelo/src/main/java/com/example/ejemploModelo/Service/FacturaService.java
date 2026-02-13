package com.example.ejemploModelo.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.example.ejemploModelo.Models.Factura;
import com.example.ejemploModelo.TaskRepository.FacturaRepository;

@Service
public class FacturaService {
    private final FacturaRepository serviceRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.serviceRepository = facturaRepository;
    }

    public Factura guardarFactura(Factura factura) {
        // Calcular impuestos basados en el país del usuario de la suscripción
        if (factura.getSuscripcion() != null && factura.getSuscripcion().getUsuario() != null) {
            var pais = factura.getSuscripcion().getUsuario().getPais();
            double monto = factura.getMonto() != null ? factura.getMonto() : 0.0;
            
            // Calcular monto de impuesto
            double montoImpuesto = TaxCalculator.calcularMonto(monto, pais);
            factura.setImpuesto(montoImpuesto);
            
            // Calcular monto total con impuestos
            double montoTotal = TaxCalculator.calcularMontoConImpuesto(monto, pais);
            factura.setMontoConImpuesto(montoTotal);
        }
        
        return serviceRepository.save(factura);
    }

    public List<Factura> listarFacturas() {
        return serviceRepository.findAll();
    }

    public Optional<Factura> obtenerPorId(Long id) {
        return serviceRepository.findById(id);
    }

    public void eliminarFactura(Long id) {
        serviceRepository.deleteById(id);
    }

    // Métodos de filtrado por fecha
    
    public List<Factura> obtenerFacturasPorFecha(LocalDate fecha) {
        return serviceRepository.findByFechaEmision(fecha);
    }

    public List<Factura> obtenerFacturasPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return serviceRepository.findByFechaRange(fechaInicio, fechaFin);
    }

    public List<Factura> obtenerFacturasDesde(LocalDate fechaInicio) {
        return serviceRepository.findByFechaEmisionGreaterThanEqual(fechaInicio);
    }

    public List<Factura> obtenerFacturasHasta(LocalDate fechaFin) {
        return serviceRepository.findByFechaEmisionLessThanEqual(fechaFin);
    }
    
    // Métodos de filtrado por monto
    
    public List<Factura> obtenerFacturasPorMontoMinimo(Double montoMin) {
        return serviceRepository.findByMontoGreaterThanEqual(montoMin);
    }
    
    public List<Factura> obtenerFacturasPorMontoMaximo(Double montoMax) {
        return serviceRepository.findByMontoLessThanEqual(montoMax);
    }
    
    public List<Factura> obtenerFacturasPorRangoMonto(Double montoMin, Double montoMax) {
        return serviceRepository.findByMontoBetween(montoMin, montoMax);
    }
    
    // Método combinado con filtros de fecha y monto
    public List<Factura> obtenerFacturasFiltradas(LocalDate fechaInicio, LocalDate fechaFin, 
                                                   Double montoMin, Double montoMax) {
        return serviceRepository.findByFechaYMontoRange(fechaInicio, fechaFin, montoMin, montoMax);
    }
}
