package com.example.ejemploModelo.TaskController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ejemploModelo.Models.Factura;
import com.example.ejemploModelo.Service.FacturaService;
import com.example.ejemploModelo.Service.SuscripcionService;

@Controller
@RequestMapping("/facturas")
public class FacturaController {
    private final FacturaService facturaService;
    private final SuscripcionService suscripcionService;

    public FacturaController(FacturaService facturaService, SuscripcionService suscripcionService) {
        this.facturaService = facturaService;
        this.suscripcionService = suscripcionService;
    }

    // Mostrar formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("factura", new Factura());
        model.addAttribute("suscripciones", suscripcionService.listarSuscripciones());
        return "Views/Factura/formulario_factura";
    }

    // Guardar factura
    @PostMapping("/guardar")
    public String guardarFactura(@RequestParam Long suscripcion, @ModelAttribute Factura factura) {
        try {
            var suscripcionObj = suscripcionService.listarSuscripciones().stream()
                .filter(s -> s.getId().equals(suscripcion))
                .findFirst()
                .orElse(null);
            
            if (suscripcionObj == null) {
                return "redirect:/facturas/nuevo?error=true";
            }
            
            factura.setSuscripcion(suscripcionObj);
            facturaService.guardarFactura(factura);
            return "redirect:/facturas/listar";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/facturas/nuevo?error=true";
        }
    }

    // Listar facturas
    @GetMapping("/listar")
    public String listarFacturas(Model model) {
        model.addAttribute("facturas", facturaService.listarFacturas());
        return "Views/Factura/lista_facturas";
    }

    // Mostrar detalle de la factura
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model) {
        var factura = facturaService.obtenerPorId(id);
        if (factura.isEmpty()) {
            return "redirect:/facturas/listar";
        }
        model.addAttribute("factura", factura.get());
        return "Views/Factura/detalle_factura";
    }

    // Mostrar formulario de edición
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        var factura = facturaService.obtenerPorId(id);
        if (factura.isEmpty()) {
            return "redirect:/facturas/listar";
        }
        model.addAttribute("factura", factura.get());
        model.addAttribute("suscripciones", suscripcionService.listarSuscripciones());
        return "Views/Factura/editar_factura";
    }

    // Guardar cambios de factura
    @PostMapping("/{id}/actualizar")
    public String actualizarFactura(@PathVariable Long id, @RequestParam Long suscripcion, 
                                    @ModelAttribute Factura factura) {
        try {
            var suscripcionObj = suscripcionService.listarSuscripciones().stream()
                .filter(s -> s.getId().equals(suscripcion))
                .findFirst()
                .orElse(null);
            
            if (suscripcionObj == null) {
                return "redirect:/facturas/" + id + "/editar?error=true";
            }
            
            factura.setId(id);
            factura.setSuscripcion(suscripcionObj);
            facturaService.guardarFactura(factura);
            return "redirect:/facturas/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/facturas/" + id + "/editar?error=true";
        }
    }

    // Eliminar factura
    @GetMapping("/{id}/eliminar")
    public String eliminarFactura(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
        return "redirect:/facturas/listar";
    }
}
