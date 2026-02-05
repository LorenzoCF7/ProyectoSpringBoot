package com.example.ejemploModelo.TaskController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ejemploModelo.Models.PagoTarjeta;
import com.example.ejemploModelo.Models.PagoPaypa;
import com.example.ejemploModelo.Models.PagoTransferencia;
import com.example.ejemploModelo.Service.PagoService;
import com.example.ejemploModelo.Service.FacturaService;

@Controller
@RequestMapping("/pagos")
public class PagoController {
    private final PagoService pagoService;
    private final FacturaService facturaService;

    public PagoController(PagoService pagoService, FacturaService facturaService) {
        this.pagoService = pagoService;
        this.facturaService = facturaService;
    }

    // Mostrar formulario para seleccionar tipo de pago
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        try {
            model.addAttribute("facturas", facturaService.listarFacturas());
            return "Views/Pago/formulario_tipo_pago";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar las facturas. Por favor, verifica que todas las facturas tengan suscripciones válidas.");
            return "redirect:/pagos/listar";
        }
    }

    // Mostrar formulario de pago por tarjeta
    @GetMapping("/tarjeta/nuevo")
    public String mostrarFormularioTarjeta(Model model) {
        try {
            model.addAttribute("pago", new PagoTarjeta());
            model.addAttribute("facturas", facturaService.listarFacturas());
            return "Views/Pago/formulario_pago_tarjeta";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pagos/listar";
        }
    }

    // Mostrar formulario de pago por PayPal
    @GetMapping("/paypal/nuevo")
    public String mostrarFormularioPaypal(Model model) {
        try {
            model.addAttribute("pago", new PagoPaypa());
            model.addAttribute("facturas", facturaService.listarFacturas());
            return "Views/Pago/formulario_pago_paypal";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pagos/listar";
        }
    }

    // Mostrar formulario de pago por transferencia
    @GetMapping("/transferencia/nuevo")
    public String mostrarFormularioTransferencia(Model model) {
        try {
            model.addAttribute("pago", new PagoTransferencia());
            model.addAttribute("facturas", facturaService.listarFacturas());
            return "Views/Pago/formulario_pago_transferencia";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pagos/listar";
        }
    }

    // Guardar pago por tarjeta
    @PostMapping("/tarjeta/guardar")
    public String guardarPagoTarjeta(@RequestParam Long factura, @ModelAttribute PagoTarjeta pago) {
        try {
            var facturaObj = facturaService.obtenerPorId(factura);
            if (facturaObj.isEmpty()) {
                return "redirect:/pagos/tarjeta/nuevo?error=true";
            }
            pago.setFactura(facturaObj.get());
            pagoService.guardarPago(pago);
            return "redirect:/pagos/listar";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pagos/tarjeta/nuevo?error=true";
        }
    }

    // Guardar pago por PayPal
    @PostMapping("/paypal/guardar")
    public String guardarPagoPaypal(@RequestParam Long factura, @ModelAttribute PagoPaypa pago) {
        try {
            var facturaObj = facturaService.obtenerPorId(factura);
            if (facturaObj.isEmpty()) {
                return "redirect:/pagos/paypal/nuevo?error=true";
            }
            pago.setFactura(facturaObj.get());
            pagoService.guardarPago(pago);
            return "redirect:/pagos/listar";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pagos/paypal/nuevo?error=true";
        }
    }

    // Guardar pago por transferencia
    @PostMapping("/transferencia/guardar")
    public String guardarPagoTransferencia(@RequestParam Long factura, @ModelAttribute PagoTransferencia pago) {
        try {
            var facturaObj = facturaService.obtenerPorId(factura);
            if (facturaObj.isEmpty()) {
                return "redirect:/pagos/transferencia/nuevo?error=true";
            }
            pago.setFactura(facturaObj.get());
            pagoService.guardarPago(pago);
            return "redirect:/pagos/listar";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pagos/transferencia/nuevo?error=true";
        }
    }

    // Listar pagos
    @GetMapping("/listar")
    public String listarPagos(Model model) {
        try {
            model.addAttribute("pagos", pagoService.listarPagos());
            return "Views/Pago/lista_pagos";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("pagos", new java.util.ArrayList<>());
            return "Views/Pago/lista_pagos";
        }
    }

    // Mostrar detalle del pago
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model) {
        var pago = pagoService.obtenerPorId(id);
        if (pago.isEmpty()) {
            return "redirect:/pagos/listar";
        }
        model.addAttribute("pago", pago.get());
        return "Views/Pago/detalle_pago";
    }

    // Eliminar pago
    @GetMapping("/{id}/eliminar")
    public String eliminarPago(@PathVariable Long id) {
        pagoService.eliminarPago(id);
        return "redirect:/pagos/listar";
    }
}