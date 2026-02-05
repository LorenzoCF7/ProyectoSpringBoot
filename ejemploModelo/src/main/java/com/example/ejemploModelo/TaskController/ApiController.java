package com.example.ejemploModelo.TaskController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ejemploModelo.Service.PlanService;
import com.example.ejemploModelo.Service.SuscripcionService;
import com.example.ejemploModelo.Service.FacturaService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    private final PlanService planService;
    private final SuscripcionService suscripcionService;
    private final FacturaService facturaService;

    public ApiController(PlanService planService, SuscripcionService suscripcionService, FacturaService facturaService) {
        this.planService = planService;
        this.suscripcionService = suscripcionService;
        this.facturaService = facturaService;
    }

    // Obtener datos de un plan por ID
    @GetMapping("/planes/{id}")
    public Map<String, Object> obtenerPlan(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        var plan = planService.obtenerPorId(id);
        if (plan.isPresent()) {
            response.put("id", plan.get().getId());
            response.put("nombre", plan.get().getNombre());
            response.put("precio", plan.get().getPrecio());
            response.put("duracionMeses", plan.get().getDuracionMeses());
        }
        return response;
    }

    // Obtener datos de una suscripción por ID
    @GetMapping("/suscripciones/{id}")
    public Map<String, Object> obtenerSuscripcion(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        var suscripcion = suscripcionService.obtenerPorId(id);
        if (suscripcion.isPresent()) {
            response.put("id", suscripcion.get().getId());
            if (suscripcion.get().getPlan() != null) {
                response.put("planPrecio", suscripcion.get().getPlan().getPrecio());
                response.put("planNombre", suscripcion.get().getPlan().getNombre());
            }
            if (suscripcion.get().getUsuario() != null) {
                response.put("usuarioEmail", suscripcion.get().getUsuario().getEmail());
            }
        }
        return response;
    }

    // Obtener datos de una factura por ID
    @GetMapping("/facturas/{id}")
    public Map<String, Object> obtenerFactura(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        var factura = facturaService.obtenerPorId(id);
        if (factura.isPresent()) {
            response.put("id", factura.get().getId());
            response.put("monto", factura.get().getMonto());
            response.put("descripcion", factura.get().getDescripcion());
        }
        return response;
    }
}
