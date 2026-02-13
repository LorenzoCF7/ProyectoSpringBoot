package com.example.ejemploModelo.Service;

import com.example.ejemploModelo.Models.AuditoriaLog;
import com.example.ejemploModelo.TaskRepository.AuditoriaLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaService {
    private final AuditoriaLogRepository auditoriaLogRepository;

    public AuditoriaService(AuditoriaLogRepository auditoriaLogRepository) {
        this.auditoriaLogRepository = auditoriaLogRepository;
    }

    public void registrar(String accion, String entidad, Long entidadId, String detalles) {
        String email = obtenerEmailUsuarioActual();
        AuditoriaLog log = new AuditoriaLog(email, accion, entidad, entidadId, detalles);
        auditoriaLogRepository.save(log);
    }

    public List<AuditoriaLog> obtenerTodos() {
        return auditoriaLogRepository.findAllByOrderByFechaDesc();
    }

    public List<AuditoriaLog> obtenerPorUsuario(String email) {
        return auditoriaLogRepository.findByUsuarioEmailOrderByFechaDesc(email);
    }

    public List<AuditoriaLog> obtenerPorEntidad(String entidad) {
        return auditoriaLogRepository.findByEntidadOrderByFechaDesc(entidad);
    }
    
    public List<AuditoriaLog> obtenerPorEntidadYId(String entidad, Long entidadId) {
        return auditoriaLogRepository.findByEntidadAndEntidadIdOrderByFechaDesc(entidad, entidadId);
    }

    private String obtenerEmailUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "sistema";
    }
}
