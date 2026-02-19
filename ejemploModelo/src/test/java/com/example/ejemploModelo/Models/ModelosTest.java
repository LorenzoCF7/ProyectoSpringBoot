package com.example.ejemploModelo.Models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CP-MOD-xx: Pruebas unitarias del modelo de dominio.
 * Verifica getters/setters y comportamiento de entidades.
 */
@DisplayName("CP-MOD: Modelos de Dominio")
class ModelosTest {

    // ─── CP-MOD-01: Usuario ──────────────────────────────────────────────────

    @Test
    @DisplayName("CP-MOD-01a: Usuario se construye con valores correctos")
    void testUsuario_setGet() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setEmail("test@test.com");
        u.setPassword("hashedPwd");
        u.setPais(Paises.ES);
        u.setActivo(true);
        u.setRol("ROLE_USER");

        assertEquals(1L, u.getId());
        assertEquals("test@test.com", u.getEmail());
        assertEquals("hashedPwd", u.getPassword());
        assertEquals(Paises.ES, u.getPais());
        assertTrue(u.isActivo());
        assertEquals("ROLE_USER", u.getRol());
    }

    @Test
    @DisplayName("CP-MOD-01b: Usuario inactivo por defecto (false)")
    void testUsuario_activoFalsePorDefecto() {
        Usuario u = new Usuario();
        assertFalse(u.isActivo());
    }

    // ─── CP-MOD-02: Plan ────────────────────────────────────────────────────

    @Test
    @DisplayName("CP-MOD-02a: Plan almacena nombre, precio y duración")
    void testPlan_setGet() {
        Plan p = new Plan();
        p.setId(5L);
        p.setNombre("Pro");
        p.setPrecio(19.99);
        p.setDuracionMeses(6);
        p.setDescripcion("Plan semestral");

        assertEquals(5L, p.getId());
        assertEquals("Pro", p.getNombre());
        assertEquals(19.99, p.getPrecio(), 0.001);
        assertEquals(6, p.getDuracionMeses());
        assertEquals("Plan semestral", p.getDescripcion());
    }

    // ─── CP-MOD-03: Suscripcion ──────────────────────────────────────────────

    @Test
    @DisplayName("CP-MOD-03a: Suscripcion almacena estado y fechas")
    void testSuscripcion_setGet() {
        Suscripcion s = new Suscripcion();
        s.setId(2L);
        s.setEstado(EstadoSuscripcion.ACTIVA);
        s.setFechaInicio(LocalDate.of(2025, 1, 1));
        s.setFechaFin(LocalDate.of(2025, 6, 30));
        s.setRenovacionAutomatica(true);

        assertEquals(EstadoSuscripcion.ACTIVA, s.getEstado());
        assertEquals(LocalDate.of(2025, 1, 1), s.getFechaInicio());
        assertEquals(LocalDate.of(2025, 6, 30), s.getFechaFin());
        assertTrue(s.isRenovacionAutomatica());
    }

    @Test
    @DisplayName("CP-MOD-03b: renovacionAutomatica es false por defecto")
    void testSuscripcion_renovacionAutomaticaFalsePorDefecto() {
        Suscripcion s = new Suscripcion();
        assertFalse(s.isRenovacionAutomatica());
    }

    // ─── CP-MOD-04: Factura ─────────────────────────────────────────────────

    @Test
    @DisplayName("CP-MOD-04a: Factura almacena monto, impuesto y estado de pago")
    void testFactura_setGet() {
        Factura f = new Factura();
        f.setId(3L);
        f.setMonto(100.0);
        f.setImpuesto(21.0);
        f.setMontoConImpuesto(121.0);
        f.setFechaEmision(LocalDate.of(2025, 5, 10));
        f.setPagada(false);
        f.setDescripcion("Cuota mensual");

        assertEquals(3L, f.getId());
        assertEquals(100.0, f.getMonto(), 0.001);
        assertEquals(21.0, f.getImpuesto(), 0.001);
        assertEquals(121.0, f.getMontoConImpuesto(), 0.001);
        assertFalse(f.isPagada());
        assertEquals("Cuota mensual", f.getDescripcion());
    }

    @Test
    @DisplayName("CP-MOD-04b: Factura no pagada por defecto")
    void testFactura_noPagadaPorDefecto() {
        Factura f = new Factura();
        assertFalse(f.isPagada());
    }

    // ─── CP-MOD-05: Perfil ──────────────────────────────────────────────────

    @Test
    @DisplayName("CP-MOD-05: Perfil almacena nombre, apellido, teléfono y usuario")
    void testPerfil_setGet() {
        Usuario u = new Usuario();
        u.setEmail("owner@test.com");

        Perfil p = new Perfil();
        p.setId(10L);
        p.setNombre("Lorenzo");
        p.setApellido("Castro");
        p.setTelefono("+34 600 000 000");
        p.setUsuario(u);

        assertEquals("Lorenzo", p.getNombre());
        assertEquals("Castro", p.getApellido());
        assertEquals("+34 600 000 000", p.getTelefono());
        assertEquals("owner@test.com", p.getUsuario().getEmail());
    }

    // ─── CP-MOD-06: Enums ───────────────────────────────────────────────────

    @Test
    @DisplayName("CP-MOD-06a: EstadoSuscripcion contiene ACTIVA, CANCELADA, MOROSA")
    void testEstadoSuscripcionEnum() {
        EstadoSuscripcion[] estados = EstadoSuscripcion.values();
        assertEquals(3, estados.length);
        assertArrayEquals(
                new EstadoSuscripcion[]{EstadoSuscripcion.ACTIVA, EstadoSuscripcion.CANCELADA, EstadoSuscripcion.MOROSA},
                estados
        );
    }

    @Test
    @DisplayName("CP-MOD-06b: Paises contiene ES, FR, DE, IT, PT, UK")
    void testPaisesEnum() {
        Paises[] paises = Paises.values();
        assertEquals(6, paises.length);
    }
}
