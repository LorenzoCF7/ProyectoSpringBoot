package com.example.ejemploModelo.Service;

import com.example.ejemploModelo.Models.Plan;
import com.example.ejemploModelo.TaskRepository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de Planes")
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    private Plan planBasico;
    private Plan planPremium;

    @BeforeEach
    void setUp() {
        planBasico = new Plan();
        planBasico.setId(1L);
        planBasico.setNombre("Básico");
        planBasico.setPrecio(9.99);
        planBasico.setDuracionMeses(1);
        planBasico.setDescripcion("Plan de un mes");

        planPremium = new Plan();
        planPremium.setId(2L);
        planPremium.setNombre("Premium");
        planPremium.setPrecio(29.99);
        planPremium.setDuracionMeses(12);
        planPremium.setDescripcion("Plan anual");
    }

    //  guardar 

    @Test
    @DisplayName("guardarPlan persiste y retorna el plan")
    void testGuardarPlan() {
        when(planRepository.save(any())).thenReturn(planBasico);

        Plan resultado = planService.guardarPlan(planBasico);

        assertNotNull(resultado);
        assertEquals("Básico", resultado.getNombre());
        verify(planRepository).save(planBasico);
    }

    @Test
    @DisplayName("El plan premium tiene 12 meses de duración")
    void testGuardarPlan_duracion12Meses() {
        when(planRepository.save(any())).thenReturn(planPremium);

        Plan resultado = planService.guardarPlan(planPremium);

        assertEquals(12, resultado.getDuracionMeses());
    }

    //  listar 

    @Test
    @DisplayName("listarPlanes retorna todos los planes")
    void testListarPlanes() {
        when(planRepository.findAll()).thenReturn(List.of(planBasico, planPremium));

        List<Plan> planes = planService.listarPlanes();

        assertEquals(2, planes.size());
    }

    @Test
    @DisplayName("listarPlanes retorna lista vacía cuando no hay planes")
    void testListarPlanes_vacio() {
        when(planRepository.findAll()).thenReturn(List.of());

        List<Plan> planes = planService.listarPlanes();

        assertTrue(planes.isEmpty());
    }

    //  obtener por id

    @Test
    @DisplayName("obtenerPorId retorna el plan cuando existe")
    void testObtenerPorId_existe() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(planBasico));

        Optional<Plan> resultado = planService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Básico", resultado.get().getNombre());
    }

    @Test
    @DisplayName("obtenerPorId retorna vacío cuando no existe")
    void testObtenerPorId_noExiste() {
        when(planRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Plan> resultado = planService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    //  eliminar

    @Test
    @DisplayName("eliminarPlan invoca deleteById exactamente una vez")
    void testEliminarPlan() {
        doNothing().when(planRepository).deleteById(1L);

        planService.eliminarPlan(1L);

        verify(planRepository, times(1)).deleteById(1L);
    }
}
