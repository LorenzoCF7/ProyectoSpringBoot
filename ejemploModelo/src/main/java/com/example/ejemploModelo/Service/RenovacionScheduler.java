package com.example.ejemploModelo.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.ejemploModelo.Models.Plan;
import com.example.ejemploModelo.Models.Suscripcion;
import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.Models.EstadoSuscripcion;
import com.example.ejemploModelo.TaskRepository.SuscripcionRepository;

@Component
public class RenovacionScheduler {
    private final SuscripcionRepository suscripcionRepository;

    public RenovacionScheduler(SuscripcionRepository suscripcionRepository) {
        this.suscripcionRepository = suscripcionRepository;
    }

    // Ejecuta una vez al día a las 00:05
    @Scheduled(cron = "0 5 0 * * *")
    public void procesarRenovaciones() {
        LocalDate hoy = LocalDate.now();
        List<Suscripcion> pendientes = suscripcionRepository.findByRenovacionAutomaticaTrueAndFechaFinLessThanEqual(hoy);
        for (Suscripcion s : pendientes) {
            try {
                Plan plan = s.getPlan();
                Usuario usuario = s.getUsuario();

                // Actualizar suscripción existente (ahora es OneToOne con usuario)
                if (plan != null && usuario != null) {
                    LocalDate inicio = (s.getFechaFin() != null) ? s.getFechaFin().plusDays(1) : hoy;
                    s.setFechaInicio(inicio);
                    s.setFechaFin(inicio.plusMonths(plan.getDuracionMeses()));
                    s.setEstado(EstadoSuscripcion.ACTIVA);
                    // Mantener la preferencia de renovación automática
                    s.setRenovacionAutomatica(true);
                    suscripcionRepository.save(s);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
