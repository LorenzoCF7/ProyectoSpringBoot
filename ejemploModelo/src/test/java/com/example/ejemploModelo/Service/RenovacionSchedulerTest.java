package com.example.ejemploModelo.Service;

import com.example.ejemploModelo.Models.*;
import com.example.ejemploModelo.TaskRepository.SuscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CP-SCH: Scheduler de Renovación Automática")
class RenovacionSchedulerTest {

    @Mock
    private SuscripcionRepository suscripcionRepository;

    @InjectMocks
    private RenovacionScheduler scheduler;

    private Plan plan3Meses;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        plan3Meses = new Plan();
        plan3Meses.setDuracionMeses(3);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("renovar@test.com");
    }

    //  Renovación exitosa

    @Test
    @DisplayName("Suscripción vencida se renueva: fechaInicio = fechaFin + 1 día")
    void testProcesarRenovaciones_nuevasFechas() {
        LocalDate finAnterior = LocalDate.of(2025, 3, 31);

        Suscripcion s = new Suscripcion();
        s.setFechaInicio(LocalDate.of(2025, 1, 1));
        s.setFechaFin(finAnterior);
        s.setEstado(EstadoSuscripcion.MOROSA);
        s.setPlan(plan3Meses);
        s.setUsuario(usuario);
        s.setRenovacionAutomatica(true);

        when(suscripcionRepository.findByRenovacionAutomaticaTrueAndFechaFinLessThanEqual(any()))
                .thenReturn(List.of(s));
        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        scheduler.procesarRenovaciones();

        ArgumentCaptor<Suscripcion> captor = ArgumentCaptor.forClass(Suscripcion.class);
        verify(suscripcionRepository).save(captor.capture());

        Suscripcion renovada = captor.getValue();
        assertEquals(LocalDate.of(2025, 4, 1), renovada.getFechaInicio());
        assertEquals(LocalDate.of(2025, 7, 1), renovada.getFechaFin());
        assertEquals(EstadoSuscripcion.ACTIVA, renovada.getEstado());
        assertTrue(renovada.isRenovacionAutomatica());
    }

    @Test
    @DisplayName("Se renuevan múltiples suscripciones en la misma ejecución")
    void testProcesarRenovaciones_multiplesRenovaciones() {
        Suscripcion s1 = crearSuscripcionVencida(LocalDate.of(2025, 1, 31));
        Suscripcion s2 = crearSuscripcionVencida(LocalDate.of(2025, 2, 28));

        when(suscripcionRepository.findByRenovacionAutomaticaTrueAndFechaFinLessThanEqual(any()))
                .thenReturn(List.of(s1, s2));
        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        scheduler.procesarRenovaciones();

        verify(suscripcionRepository, times(2)).save(any());
    }

    //  Sin suscripciones a renovar

    @Test
    @DisplayName("Sin suscripciones vencidas, no se llama a save")
    void testProcesarRenovaciones_sinPendientes() {
        when(suscripcionRepository.findByRenovacionAutomaticaTrueAndFechaFinLessThanEqual(any()))
                .thenReturn(Collections.emptyList());

        scheduler.procesarRenovaciones();

        verify(suscripcionRepository, never()).save(any());
    }

    //  Suscripción sin plan no lanza excepción

    @Test
    @DisplayName("Suscripción sin plan es ignorada sin lanzar excepción")
    void testProcesarRenovaciones_sinPlan_noLanzaExcepcion() {
        Suscripcion s = new Suscripcion();
        s.setPlan(null);
        s.setUsuario(usuario);
        s.setFechaFin(LocalDate.of(2024, 12, 31));
        s.setRenovacionAutomatica(true);

        when(suscripcionRepository.findByRenovacionAutomaticaTrueAndFechaFinLessThanEqual(any()))
                .thenReturn(List.of(s));

        assertDoesNotThrow(() -> scheduler.procesarRenovaciones());
        verify(suscripcionRepository, never()).save(any());
    }

    //  Estado queda ACTIVA tras renovación

    @Test
    @DisplayName("El estado queda ACTIVA independientemente del estado previo")
    void testProcesarRenovaciones_estadoActivaTrasRenovacion() {
        Suscripcion s = crearSuscripcionVencida(LocalDate.of(2025, 5, 15));
        s.setEstado(EstadoSuscripcion.CANCELADA); // estado previo diferente

        when(suscripcionRepository.findByRenovacionAutomaticaTrueAndFechaFinLessThanEqual(any()))
                .thenReturn(List.of(s));
        ArgumentCaptor<Suscripcion> captor = ArgumentCaptor.forClass(Suscripcion.class);
        when(suscripcionRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        scheduler.procesarRenovaciones();

        assertEquals(EstadoSuscripcion.ACTIVA, captor.getValue().getEstado());
    }

    //  Helpers 

    private Suscripcion crearSuscripcionVencida(LocalDate fechaFin) {
        Suscripcion s = new Suscripcion();
        s.setFechaFin(fechaFin);
        s.setEstado(EstadoSuscripcion.MOROSA);
        s.setPlan(plan3Meses);
        s.setUsuario(usuario);
        s.setRenovacionAutomatica(true);
        return s;
    }
}
