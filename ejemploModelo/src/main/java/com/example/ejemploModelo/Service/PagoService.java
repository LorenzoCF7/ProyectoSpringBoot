package com.example.ejemploModelo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ejemploModelo.Models.Pago;
import com.example.ejemploModelo.TaskRepository.PagoRepository;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public Pago guardarPago(Pago pago) {
        return pagoRepository.save(pago);
    }

    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> obtenerPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public void eliminarPago(Long id) {
        pagoRepository.deleteById(id);
    }
}