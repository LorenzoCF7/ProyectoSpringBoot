package com.example.ejemploModelo.TaskController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ejemploModelo.Models.Suscripcion;
import com.example.ejemploModelo.Service.SuscripcionService;
import com.example.ejemploModelo.Service.UsuarioService;
import com.example.ejemploModelo.Service.PlanService;

@Controller
@RequestMapping("/suscripciones")
public class SuscripcionController {
    private final SuscripcionService suscripcionService;
    private final UsuarioService usuarioService;
    private final PlanService planService;

    public SuscripcionController(SuscripcionService suscripcionService, UsuarioService usuarioService, PlanService planService) {
        this.suscripcionService = suscripcionService;
        this.usuarioService = usuarioService;
        this.planService = planService;
    }

    // Mostrar formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("suscripcion", new Suscripcion());
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("planes", planService.listarPlanes());
        return "Views/Suscripcion/formulario_suscripcion";
    }

    // Guardar suscripcion
    @PostMapping("/guardar")
    public String guardarSuscripcion(@RequestParam Long usuario, @RequestParam Long plan, 
                                      @ModelAttribute Suscripcion suscripcion) {
        try {
            // Cargar el usuario y plan desde la BD por su ID
            var usuarioObj = usuarioService.listarUsuarios().stream()
                .filter(u -> u.getId().equals(usuario))
                .findFirst()
                .orElse(null);
            
            var planObj = planService.listarPlanes().stream()
                .filter(p -> p.getId().equals(plan))
                .findFirst()
                .orElse(null);
            
            if (usuarioObj == null || planObj == null) {
                return "redirect:/suscripciones/nuevo?error=true";
            }
            
            suscripcion.setUsuario(usuarioObj);
            suscripcion.setPlan(planObj);
            suscripcionService.guardarSuscripcion(suscripcion);
            return "redirect:/suscripciones/listar";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/suscripciones/nuevo?error=true";
        }
    }

    // Listar suscripciones
    @GetMapping("/listar")
    public String listarSuscripciones(Model model) {
        model.addAttribute("suscripciones", suscripcionService.listarSuscripciones());
        return "Views/Suscripcion/lista_suscripciones";
    }

    // Mostrar detalle de la suscripción
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model) {
        var suscripcion = suscripcionService.obtenerPorId(id);
        if (suscripcion.isEmpty()) {
            return "redirect:/suscripciones/listar";
        }
        model.addAttribute("suscripcion", suscripcion.get());
        return "Views/Suscripcion/detalle_suscripcion";
    }

    // Mostrar formulario de edición
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        var suscripcion = suscripcionService.obtenerPorId(id);
        if (suscripcion.isEmpty()) {
            return "redirect:/suscripciones/listar";
        }
        model.addAttribute("suscripcion", suscripcion.get());
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("planes", planService.listarPlanes());
        return "Views/Suscripcion/editar_suscripcion";
    }

    // Guardar cambios de suscripción
    @PostMapping("/{id}/actualizar")
    public String actualizarSuscripcion(@PathVariable Long id, @RequestParam Long usuario, @RequestParam Long plan, 
                                         @ModelAttribute Suscripcion suscripcion) {
        try {
            var usuarioObj = usuarioService.listarUsuarios().stream()
                .filter(u -> u.getId().equals(usuario))
                .findFirst()
                .orElse(null);
            
            var planObj = planService.listarPlanes().stream()
                .filter(p -> p.getId().equals(plan))
                .findFirst()
                .orElse(null);
            
            if (usuarioObj == null || planObj == null) {
                return "redirect:/suscripciones/" + id + "/editar?error=true";
            }
            
            suscripcion.setId(id);
            suscripcion.setUsuario(usuarioObj);
            suscripcion.setPlan(planObj);
            suscripcionService.guardarSuscripcion(suscripcion);
            return "redirect:/suscripciones/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/suscripciones/" + id + "/editar?error=true";
        }
    }

    // Eliminar suscripción
    @GetMapping("/{id}/eliminar")
    public String eliminarSuscripcion(@PathVariable Long id) {
        suscripcionService.eliminarSuscripcion(id);
        return "redirect:/suscripciones/listar";
    }
}
