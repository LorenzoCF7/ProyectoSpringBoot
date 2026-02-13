package com.example.ejemploModelo.Service;

import com.example.ejemploModelo.Models.Paises;

public class TaxCalculator {
    // Tasas de impuestos por país (en porcentaje)
    private static final double TASA_ESPAÑA = 21.0;
    private static final double TASA_FRANCIA = 20.0;
    private static final double TASA_ALEMANIA = 19.0;
    private static final double TASA_ITALIA = 22.0;
    private static final double TASA_PORTUGAL = 23.0;
    private static final double TASA_REINO_UNIDO = 20.0;


    public static double obtenerTasaImpuesto(Paises pais) {
        if (pais == null) {
            return TASA_ESPAÑA; // Usa España como valor por defecto
        }
        
        return switch (pais) {
            case ES -> TASA_ESPAÑA;
            case FR -> TASA_FRANCIA;
            case DE -> TASA_ALEMANIA;
            case IT -> TASA_ITALIA;
            case PT -> TASA_PORTUGAL;
            case UK -> TASA_REINO_UNIDO;
            default -> TASA_ESPAÑA;
        };
    }


    public static double calcularMonto(double montoBase, Paises pais) {
        double tasa = obtenerTasaImpuesto(pais) / 100.0;
        return montoBase * tasa;
    }

 
    public static double calcularMontoConImpuesto(double montoBase, Paises pais) {
        return montoBase + calcularMonto(montoBase, pais);
    }


    public static String obtenerDescripcionImpuesto(Paises pais) {
        double tasa = obtenerTasaImpuesto(pais);
        String tipoImpuesto = obtenerTipoImpuesto(pais);
        return tipoImpuesto + " " + String.format("%.1f", tasa) + "%";
    }

 
    private static String obtenerTipoImpuesto(Paises pais) {
        return switch (pais) {
            case ES, IT, PT -> "IVA";
            case FR -> "TVA";
            case DE -> "MwSt";
            case UK -> "VAT";
            default -> "IVA";
        };
    }
}
