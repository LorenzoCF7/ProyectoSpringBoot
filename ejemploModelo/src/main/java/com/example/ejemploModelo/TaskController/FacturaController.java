package com.example.ejemploModelo.TaskController;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.List;

import com.example.ejemploModelo.Models.Factura;
import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.Service.AuditoriaService;
import com.example.ejemploModelo.Service.FacturaService;
import com.example.ejemploModelo.Service.SuscripcionService;
import com.example.ejemploModelo.Service.TaxCalculator;
import com.example.ejemploModelo.Service.UsuarioService;

@Controller
@RequestMapping("/facturas")
public class FacturaController {
    private final FacturaService facturaService;
    private final SuscripcionService suscripcionService;
    private final AuditoriaService auditoriaService;
    private final UsuarioService usuarioService;

    public FacturaController(FacturaService facturaService, SuscripcionService suscripcionService, 
                              AuditoriaService auditoriaService, UsuarioService usuarioService) {
        this.facturaService = facturaService;
        this.suscripcionService = suscripcionService;
        this.auditoriaService = auditoriaService;
        this.usuarioService = usuarioService;
    }

    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Usuario getUsuarioActual(Authentication auth) {
        return usuarioService.obtenerPorEmail(auth.getName()).orElse(null);
    }

    // Mostrar formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model, Authentication auth) {
        model.addAttribute("factura", new Factura());
        if (esAdmin(auth)) {
            model.addAttribute("suscripciones", suscripcionService.listarSuscripciones());
        } else {
            Usuario usuario = getUsuarioActual(auth);
            var propias = suscripcionService.listarSuscripciones().stream()
                .filter(s -> s.getUsuario() != null && s.getUsuario().getId().equals(usuario.getId()))
                .toList();
            model.addAttribute("suscripciones", propias);
        }
        model.addAttribute("taxCalculator", TaxCalculator.class);
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
            auditoriaService.registrar("CREAR", "Factura", factura.getId(),
                "Factura creada - Monto: " + factura.getMonto());
            return "redirect:/facturas/listar";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/facturas/nuevo?error=true";
        }
    }

    // Listar facturas
    @GetMapping("/listar")
    public String listarFacturas(@RequestParam(required = false) LocalDate fechaInicio,
                                 @RequestParam(required = false) LocalDate fechaFin,
                                 @RequestParam(required = false) Double montoMin,
                                 @RequestParam(required = false) Double montoMax,
                                 Model model, Authentication auth) {
        List<Factura> facturas;
        
        // Si hay algún filtro, usar el método combinado
        if (fechaInicio != null || fechaFin != null || montoMin != null || montoMax != null) {
            facturas = facturaService.obtenerFacturasFiltradas(fechaInicio, fechaFin, montoMin, montoMax);
        } else {
            facturas = facturaService.listarFacturas();
        }
        
        // Si no es admin, filtrar solo las facturas del usuario
        if (!esAdmin(auth)) {
            Usuario usuario = getUsuarioActual(auth);
            facturas = facturas.stream()
                .filter(f -> f.getSuscripcion() != null && f.getSuscripcion().getUsuario() != null 
                    && f.getSuscripcion().getUsuario().getId().equals(usuario.getId()))
                .toList();
        }
        
        model.addAttribute("facturas", facturas);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("montoMin", montoMin);
        model.addAttribute("montoMax", montoMax);
        return "Views/Factura/lista_facturas";
    }

    // Mostrar detalle de la factura
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model) {
        try {
            var factura = facturaService.obtenerPorId(id);
            if (factura.isEmpty()) {
                return "redirect:/facturas/listar";
            }
            model.addAttribute("factura", factura.get());
            
            // Pasar información de impuestos a la vista
            if (factura.get().getSuscripcion() != null 
                && factura.get().getSuscripcion().getUsuario() != null
                && factura.get().getSuscripcion().getUsuario().getPais() != null) {
                var pais = factura.get().getSuscripcion().getUsuario().getPais();
                model.addAttribute("pais", pais);
                model.addAttribute("descripcionImpuesto", TaxCalculator.obtenerDescripcionImpuesto(pais));
            }
            
            return "Views/Factura/detalle_factura";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/facturas/listar";
        }
    }

    // Mostrar formulario de edición
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            var factura = facturaService.obtenerPorId(id);
            if (factura.isEmpty()) {
                return "redirect:/facturas/listar";
            }
            model.addAttribute("factura", factura.get());
            model.addAttribute("suscripciones", suscripcionService.listarSuscripciones());
            
            // Pasar información de impuestos a la vista
            if (factura.get().getSuscripcion() != null 
                && factura.get().getSuscripcion().getUsuario() != null
                && factura.get().getSuscripcion().getUsuario().getPais() != null) {
                var pais = factura.get().getSuscripcion().getUsuario().getPais();
                model.addAttribute("pais", pais);
                model.addAttribute("descripcionImpuesto", TaxCalculator.obtenerDescripcionImpuesto(pais));
            }
            
            return "Views/Factura/editar_factura";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/facturas/listar";
        }
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
            auditoriaService.registrar("EDITAR", "Factura", id,
                "Factura editada - Monto: " + factura.getMonto());
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
        auditoriaService.registrar("ELIMINAR", "Factura", id, "Factura eliminada");
        return "redirect:/facturas/listar";
    }
}
