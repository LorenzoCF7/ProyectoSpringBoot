package com.example.ejemploModelo.Service;

import com.example.ejemploModelo.Models.EstadoSuscripcion;
import com.example.ejemploModelo.Models.Plan;
import com.example.ejemploModelo.Models.Suscripcion;
import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.TaskRepository.SuscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de Suscripciones")
class SuscripcionServiceTest {

    @Mock
    private SuscripcionRepository suscripcionRepository;

    @InjectMocks
    private SuscripcionService suscripcionService;

    private Plan plan3Meses;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        plan3Meses = new Plan();
        plan3Meses.setId(1L);
        plan3Meses.setNombre("Básico");
        plan3Meses.setPrecio(9.99);
        plan3Meses.setDuracionMeses(3);

        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setEmail("user@test.com");
    }

    // Fecha de inicio

    @Test
    @DisplayName("Se asigna fecha de inicio cuando es null")
    void testGuardar_fechaInicioAsignadaAutomaticamente() {
        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);
        s.setPlan(plan3Meses);

        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Suscripcion resultado = suscripcionService.guardarSuscripcion(s);

        assertNotNull(resultado.getFechaInicio());
        assertEquals(LocalDate.now(), resultado.getFechaInicio());
    }

    @Test
    @DisplayName("Se respeta la fecha de inicio si ya viene definida")
    void testGuardar_fechaInicioRespetada() {
        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);
        s.setPlan(plan3Meses);
        LocalDate fechaExplicita = LocalDate.of(2025, 1, 1);
        s.setFechaInicio(fechaExplicita);

        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Suscripcion resultado = suscripcionService.guardarSuscripcion(s);

        assertEquals(fechaExplicita, resultado.getFechaInicio());
    }

    // CP-SUS-02: Fecha de fin

    @Test
    @DisplayName("Fecha fin = fechaInicio + duración del plan en meses")
    void testGuardar_fechaFinCalculadaPorPlan() {
        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);
        s.setPlan(plan3Meses);
        LocalDate inicio = LocalDate.of(2025, 6, 1);
        s.setFechaInicio(inicio);

        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Suscripcion resultado = suscripcionService.guardarSuscripcion(s);

        assertEquals(LocalDate.of(2025, 9, 1), resultado.getFechaFin());
    }

    @Test
    @DisplayName("Fecha fin no se sobreescribe si ya está definida")
    void testGuardar_fechaFinRespetada() {
        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);
        s.setPlan(plan3Meses);
        LocalDate inicio = LocalDate.of(2025, 6, 1);
        LocalDate finExplicito = LocalDate.of(2025, 12, 31);
        s.setFechaInicio(inicio);
        s.setFechaFin(finExplicito);

        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Suscripcion resultado = suscripcionService.guardarSuscripcion(s);

        assertEquals(finExplicito, resultado.getFechaFin());
    }

    @Test
    @DisplayName("Sin plan, la fecha fin queda null")
    void testGuardar_sinPlanFechaFinNull() {
        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);

        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Suscripcion resultado = suscripcionService.guardarSuscripcion(s);

        assertNull(resultado.getFechaFin());
    }

    //  Estado por defecto

    @Test
    @DisplayName("Estado por defecto es ACTIVA cuando es null")
    void testGuardar_estadoDefecto() {
        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);

        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Suscripcion resultado = suscripcionService.guardarSuscripcion(s);

        assertEquals(EstadoSuscripcion.ACTIVA, resultado.getEstado());
    }

    @Test
    @DisplayName("Se respeta el estado CANCELADA si ya viene definido")
    void testGuardar_estadoNoSobreescrito() {
        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);
        s.setEstado(EstadoSuscripcion.CANCELADA);

        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Suscripcion resultado = suscripcionService.guardarSuscripcion(s);

        assertEquals(EstadoSuscripcion.CANCELADA, resultado.getEstado());
    }

    //  Búsqueda por usuario

    @Test
    @DisplayName("obtenerPorUsuario retorna la suscripción del usuario")
    void testObtenerPorUsuario() {
        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);
        when(suscripcionRepository.findByUsuario(usuario)).thenReturn(Optional.of(s));

        Optional<Suscripcion> resultado = suscripcionService.obtenerPorUsuario(usuario);

        assertTrue(resultado.isPresent());
    }

    //  Plan con 12 meses

    @Test
    @DisplayName("CP-SUS-05: Plan de 12 meses calcula fecha fin correctamente")
    void testGuardar_plan12Meses() {
        Plan plan12 = new Plan();
        plan12.setDuracionMeses(12);

        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);
        s.setPlan(plan12);
        LocalDate inicio = LocalDate.of(2025, 1, 15);
        s.setFechaInicio(inicio);

        when(suscripcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Suscripcion resultado = suscripcionService.guardarSuscripcion(s);

        assertEquals(LocalDate.of(2026, 1, 15), resultado.getFechaFin());
    }
}
