/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ejemploModelo.TaskController;

import com.example.ejemploModelo.Models.Plan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.ejemploModelo.Service.AuditoriaService;
import com.example.ejemploModelo.Service.PlanService;

@Controller
@RequestMapping("/planes")
public class PlanController {

    private final PlanService planService;
    private final AuditoriaService auditoriaService;

    public PlanController(PlanService planService, AuditoriaService auditoriaService) {
        this.planService = planService;
        this.auditoriaService = auditoriaService;
    }

    // Mostrar formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("plan", new Plan());
        return "Views/Plan/formulario_plan";
    }

    // Guardar plan
    @PostMapping("/guardar")
    public String guardarPlan(@ModelAttribute Plan plan) {
        planService.guardarPlan(plan);
        auditoriaService.registrar("CREAR", "Plan", plan.getId(),
            "Plan creado: " + plan.getNombre());
        return "redirect:/planes/listar";
    }

    // Listar planes
    @GetMapping("/listar")
    public String listarPlanes(Model model) {
        model.addAttribute("planes", planService.listarPlanes());
        return "Views/Plan/lista_planes";
    }

    // Mostrar detalle del plan
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model) {
        var plan = planService.obtenerPorId(id);
        if (plan.isEmpty()) {
            return "redirect:/planes/listar";
        }
        model.addAttribute("plan", plan.get());
        return "Views/Plan/detalle_plan";
    }

    // Mostrar formulario de edición
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        var plan = planService.obtenerPorId(id);
        if (plan.isEmpty()) {
            return "redirect:/planes/listar";
        }
        model.addAttribute("plan", plan.get());
        return "Views/Plan/editar_plan";
    }

    // Guardar cambios de plan
    @PostMapping("/{id}/actualizar")
    public String actualizarPlan(@PathVariable Long id, @ModelAttribute Plan plan) {
        plan.setId(id);
        planService.guardarPlan(plan);
        auditoriaService.registrar("EDITAR", "Plan", id,
            "Plan editado: " + plan.getNombre());
        return "redirect:/planes/" + id;
    }

    // Eliminar plan
    @GetMapping("/{id}/eliminar")
    public String eliminarPlan(@PathVariable Long id) {
        planService.eliminarPlan(id);
        auditoriaService.registrar("ELIMINAR", "Plan", id, "Plan eliminado");
        return "redirect:/planes/listar";
    }
}
