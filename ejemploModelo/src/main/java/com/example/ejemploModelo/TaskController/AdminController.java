package com.example.ejemploModelo.TaskController;

import com.example.ejemploModelo.Service.AuditoriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuditoriaService auditoriaService;

    public AdminController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/auditoria")
    public String panelAuditoria(@RequestParam(value = "entidad", required = false) String entidad,
                                  @RequestParam(value = "usuario", required = false) String usuario,
                                  Model model) {
        if (entidad != null && !entidad.isEmpty()) {
            model.addAttribute("logs", auditoriaService.obtenerPorEntidad(entidad));
            model.addAttribute("filtroEntidad", entidad);
        } else if (usuario != null && !usuario.isEmpty()) {
            model.addAttribute("logs", auditoriaService.obtenerPorUsuario(usuario));
            model.addAttribute("filtroUsuario", usuario);
        } else {
            model.addAttribute("logs", auditoriaService.obtenerTodos());
        }
        return "Views/Admin/auditoria";
    }
    
    @GetMapping("/auditoria/{entidad}/{id}")
    public String historialEntidad(@PathVariable String entidad, @PathVariable Long id, Model model) {
        model.addAttribute("logs", auditoriaService.obtenerPorEntidadYId(entidad, id));
        model.addAttribute("entidad", entidad);
        model.addAttribute("entidadId", id);
        return "Views/Admin/auditoria";
    }
}
