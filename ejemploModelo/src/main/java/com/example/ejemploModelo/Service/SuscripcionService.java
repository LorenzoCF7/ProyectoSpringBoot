package com.example.ejemploModelo.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.example.ejemploModelo.Models.Suscripcion;
import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.Models.EstadoSuscripcion;
import com.example.ejemploModelo.TaskRepository.SuscripcionRepository;

@Service
public class SuscripcionService {
    private final SuscripcionRepository serviceRepository;

    public SuscripcionService(SuscripcionRepository suscripcionRepository) {
        this.serviceRepository = suscripcionRepository;
    }

    public Suscripcion guardarSuscripcion(Suscripcion suscripcion) {
        // Establecer fecha de inicio si no está definida
        if (suscripcion.getFechaInicio() == null) {
            suscripcion.setFechaInicio(LocalDate.now());
        }
        
        // Calcular fecha fin basada en la duración del plan si no está definida
        if (suscripcion.getFechaFin() == null && suscripcion.getPlan() != null) {
            int duracionMeses = suscripcion.getPlan().getDuracionMeses();
            suscripcion.setFechaFin(suscripcion.getFechaInicio().plusMonths(duracionMeses));
        }
        
        // Establecer estado por defecto si no está definido
        if (suscripcion.getEstado() == null) {
            suscripcion.setEstado(EstadoSuscripcion.ACTIVA);
        }
        
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
    
    public Optional<Suscripcion> obtenerPorUsuario(Usuario usuario) {
        return serviceRepository.findByUsuario(usuario);
    }
}
