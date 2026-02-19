package com.example.ejemploModelo.Service;

import com.example.ejemploModelo.Models.Paises;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Calculadora de Impuestos")
class TaxCalculatorTest {

    // ─── CP-TAX-01: Tasa correcta por país ──────────────────────────────────

    @Test
    @DisplayName("España aplica IVA 21%")
    void testTasaEspana() {
        assertEquals(21.0, TaxCalculator.obtenerTasaImpuesto(Paises.ES));
    }

    @Test
    @DisplayName("Francia aplica TVA 20%")
    void testTasaFrancia() {
        assertEquals(20.0, TaxCalculator.obtenerTasaImpuesto(Paises.FR));
    }

    @Test
    @DisplayName("Alemania aplica MwSt 19%")
    void testTasaAlemania() {
        assertEquals(19.0, TaxCalculator.obtenerTasaImpuesto(Paises.DE));
    }

    @Test
    @DisplayName("Italia aplica IVA 22%")
    void testTasaItalia() {
        assertEquals(22.0, TaxCalculator.obtenerTasaImpuesto(Paises.IT));
    }

    @Test
    @DisplayName("Portugal aplica IVA 23%")
    void testTasaPortugal() {
        assertEquals(23.0, TaxCalculator.obtenerTasaImpuesto(Paises.PT));
    }

    @Test
    @DisplayName("Reino Unido aplica VAT 20%")
    void testTasaReinoUnido() {
        assertEquals(20.0, TaxCalculator.obtenerTasaImpuesto(Paises.UK));
    }

    //  País nulo usa España como fallback

    @Test
    @DisplayName("País null usa tasa de España (21%)")
    void testTasaPaisNull() {
        assertEquals(21.0, TaxCalculator.obtenerTasaImpuesto(null));
    }

    // ─── CP-TAX-03: Cálculo del monto de impuesto ────────────────────────────

    @Test
    @DisplayName("Monto de impuesto ES = 100 * 21% = 21.0")
    void testCalcularMontoImpuesto() {
        double impuesto = TaxCalculator.calcularMonto(100.0, Paises.ES);
        assertEquals(21.0, impuesto, 0.001);
    }

    @Test
    @DisplayName("Monto de impuesto DE = 100 * 19% = 19.0")
    void testCalcularMontoImpuestoAlemania() {
        double impuesto = TaxCalculator.calcularMonto(100.0, Paises.DE);
        assertEquals(19.0, impuesto, 0.001);
    }

    @Test
    @DisplayName("Impuesto sobre monto 0 es 0")
    void testCalcularMontoImpuestoCero() {
        double impuesto = TaxCalculator.calcularMonto(0.0, Paises.ES);
        assertEquals(0.0, impuesto, 0.001);
    }

    //  Cálculo del monto total con impuesto

    @Test
    @DisplayName("Total ES = 100 + 21% = 121.0")
    void testCalcularMontoConImpuestoEspana() {
        double total = TaxCalculator.calcularMontoConImpuesto(100.0, Paises.ES);
        assertEquals(121.0, total, 0.001);
    }

    @Test
    @DisplayName("Total IT = 100 + 22% = 122.0")
    void testCalcularMontoConImpuestoItalia() {
        double total = TaxCalculator.calcularMontoConImpuesto(100.0, Paises.IT);
        assertEquals(122.0, total, 0.001);
    }

    @Test
    @DisplayName("Total PT = 50 + 23% = 61.5")
    void testCalcularMontoConImpuestoPortugal() {
        double total = TaxCalculator.calcularMontoConImpuesto(50.0, Paises.PT);
        assertEquals(61.5, total, 0.001);
    }

    @Test
    @DisplayName("Total con null (fallback ES) = 121.0")
    void testCalcularMontoConImpuestoNull() {
        double total = TaxCalculator.calcularMontoConImpuesto(100.0, null);
        assertEquals(121.0, total, 0.001);
    }

    //  Descripción del impuesto

    @Test
    @DisplayName("Descripción ES = 'IVA 21.0%'")
    void testDescripcionImpuestoEspana() {
        String desc = TaxCalculator.obtenerDescripcionImpuesto(Paises.ES);
        assertEquals("IVA 21.0%", desc);
    }

    @Test
    @DisplayName("Descripción FR = 'TVA 20.0%'")
    void testDescripcionImpuestoFrancia() {
        String desc = TaxCalculator.obtenerDescripcionImpuesto(Paises.FR);
        assertEquals("TVA 20.0%", desc);
    }

    @Test
    @DisplayName("Descripción DE = 'MwSt 19.0%'")
    void testDescripcionImpuestoAlemania() {
        String desc = TaxCalculator.obtenerDescripcionImpuesto(Paises.DE);
        assertEquals("MwSt 19.0%", desc);
    }

    @Test
    @DisplayName("Descripción UK = 'VAT 20.0%'")
    void testDescripcionImpuestoReinoUnido() {
        String desc = TaxCalculator.obtenerDescripcionImpuesto(Paises.UK);
        assertEquals("VAT 20.0%", desc);
    }
}
