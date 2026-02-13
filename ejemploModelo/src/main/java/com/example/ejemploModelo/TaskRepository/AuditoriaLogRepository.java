package com.example.ejemploModelo.TaskRepository;

import com.example.ejemploModelo.Models.AuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {
    List<AuditoriaLog> findAllByOrderByFechaDesc();
    List<AuditoriaLog> findByUsuarioEmailOrderByFechaDesc(String usuarioEmail);
    List<AuditoriaLog> findByEntidadOrderByFechaDesc(String entidad);
    List<AuditoriaLog> findByEntidadAndEntidadIdOrderByFechaDesc(String entidad, Long entidadId);
}
