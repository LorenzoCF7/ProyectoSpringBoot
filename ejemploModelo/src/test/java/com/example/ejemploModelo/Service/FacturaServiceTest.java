package com.example.ejemploModelo.Service;

import com.example.ejemploModelo.Models.*;
import com.example.ejemploModelo.TaskRepository.FacturaRepository;
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
@DisplayName("Servicio de Facturas")
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @InjectMocks
    private FacturaService facturaService;

    private Usuario usuarioES;
    private Usuario usuarioDE;
    private Suscripcion suscripcionES;
    private Suscripcion suscripcionDE;

    @BeforeEach
    void setUp() {
        usuarioES = new Usuario();
        usuarioES.setId(1L);
        usuarioES.setPais(Paises.ES);

        usuarioDE = new Usuario();
        usuarioDE.setId(2L);
        usuarioDE.setPais(Paises.DE);

        suscripcionES = new Suscripcion();
        suscripcionES.setId(1L);
        suscripcionES.setUsuario(usuarioES);

        suscripcionDE = new Suscripcion();
        suscripcionDE.setId(2L);
        suscripcionDE.setUsuario(usuarioDE);
    }

    // Cálculo de impuesto al guardar
    @Test
    @DisplayName("Impuesto calculado para usuario ES (21%)")
    void testGuardarFactura_impuestoEspana() {
        Factura f = new Factura();
        f.setMonto(100.0);
        f.setFechaEmision(LocalDate.now());
        f.setSuscripcion(suscripcionES);

        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura resultado = facturaService.guardarFactura(f);

        assertEquals(21.0, resultado.getImpuesto(), 0.001);
    }

    @Test
    @DisplayName("Monto con impuesto para usuario ES = 121.0")
    void testGuardarFactura_montoConImpuestoEspana() {
        Factura f = new Factura();
        f.setMonto(100.0);
        f.setSuscripcion(suscripcionES);

        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura resultado = facturaService.guardarFactura(f);

        assertEquals(121.0, resultado.getMontoConImpuesto(), 0.001);
    }

    @Test
    @DisplayName("Impuesto calculado para usuario DE (19%)")
    void testGuardarFactura_impuestoAlemania() {
        Factura f = new Factura();
        f.setMonto(100.0);
        f.setSuscripcion(suscripcionDE);

        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura resultado = facturaService.guardarFactura(f);

        assertEquals(19.0, resultado.getImpuesto(), 0.001);
        assertEquals(119.0, resultado.getMontoConImpuesto(), 0.001);
    }

    // Factura sin suscripción no lanza error

    @Test
    @DisplayName("Factura sin suscripción no calcula impuesto")
    void testGuardarFactura_sinSuscripcion() {
        Factura f = new Factura();
        f.setMonto(100.0);
        // sin suscripcion

        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> facturaService.guardarFactura(f));
    }

    // Monto null se trata como 0

    @Test
    @DisplayName("Monto null se trata como 0 (sin NullPointerException)")
    void testGuardarFactura_montoNull() {
        Factura f = new Factura();
        f.setMonto(null);
        f.setSuscripcion(suscripcionES);

        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura resultado = facturaService.guardarFactura(f);

        assertEquals(0.0, resultado.getImpuesto(), 0.001);
        assertEquals(0.0, resultado.getMontoConImpuesto(), 0.001);
    }

    // Impuesto IT (22%) con monto 200

    @Test
    @DisplayName("Italia 22% sobre 200 = impuesto 44.0, total 244.0")
    void testGuardarFactura_impuestoItalia() {
        Usuario usuarioIT = new Usuario();
        usuarioIT.setPais(Paises.IT);
        Suscripcion susIT = new Suscripcion();
        susIT.setUsuario(usuarioIT);

        Factura f = new Factura();
        f.setMonto(200.0);
        f.setSuscripcion(susIT);

        when(facturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura resultado = facturaService.guardarFactura(f);

        assertEquals(44.0, resultado.getImpuesto(), 0.001);
        assertEquals(244.0, resultado.getMontoConImpuesto(), 0.001);
    }

    // obtenerPorId

    @Test
    @DisplayName("obtenerPorId retorna vacío cuando no existe")
    void testObtenerPorId_vacio() {
        when(facturaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Factura> resultado = facturaService.obtenerPorId(999L);

        assertFalse(resultado.isPresent());
    }
}
