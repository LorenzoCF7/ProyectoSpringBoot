package com.example.ejemploModelo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ejemploModelo.Models.Suscripcion;
import com.example.ejemploModelo.TaskRepository.SuscripcionRepository;

@Service
public class SuscripcionService {
    private final SuscripcionRepository serviceRepository;

    public SuscripcionService(SuscripcionRepository suscripcionRepository) {
        this.serviceRepository = suscripcionRepository;
    }

    public Suscripcion guardarSuscripcion(Suscripcion suscripcion) {
        return serviceRepository.save(suscripcion);
    }

    public List<Suscripcion> listarSuscripciones() {
        return serviceRepository.findAll();
    }

    public Optional<Suscripcion> obtenerPorId(Long id) {
        return serviceRepository.findById(id);
    }

    public void eliminarSuscripcion(Long id) {
        serviceRepository.deleteById(id);
    }
}
