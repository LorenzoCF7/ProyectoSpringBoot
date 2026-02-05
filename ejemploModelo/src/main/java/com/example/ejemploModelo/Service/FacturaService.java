package com.example.ejemploModelo.Service;
import java.util.List;
import java.util.Optional;

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
}
