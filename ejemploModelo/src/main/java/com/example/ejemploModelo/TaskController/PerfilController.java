package com.example.ejemploModelo.TaskController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ejemploModelo.Models.Perfil;
import com.example.ejemploModelo.Service.PerfilService;
import com.example.ejemploModelo.Service.UsuarioService;

@Controller
@RequestMapping("/perfiles")
public class PerfilController {
    private final PerfilService perfilService;
    private final UsuarioService usuarioService;

    public PerfilController(PerfilService perfilService, UsuarioService usuarioService) {
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;
    }

    // Mostrar formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("perfil", new Perfil());
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "Views/Perfil/formulario_perfil";
    }

    // Guardar perfil
    @PostMapping("/guardar")
    public String guardarPerfil(@ModelAttribute Perfil perfil) {
        perfilService.guardarPerfil(perfil);
        return "redirect:/perfiles/listar";
    }

    // Listar perfiles
    @GetMapping("/listar")
    public String listarPerfiles(Model model) {
        model.addAttribute("perfiles", perfilService.listarPerfiles());
        return "Views/Perfil/lista_perfiles";
    }

    // Mostrar detalle del perfil
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model) {
        var perfil = perfilService.obtenerPorId(id);
        if (perfil.isEmpty()) {
            return "redirect:/perfiles/listar";
        }
        model.addAttribute("perfil", perfil.get());
        return "Views/Perfil/detalle_perfil";
    }

    // Mostrar formulario de edición
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        var perfil = perfilService.obtenerPorId(id);
        if (perfil.isEmpty()) {
            return "redirect:/perfiles/listar";
        }
        model.addAttribute("perfil", perfil.get());
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "Views/Perfil/editar_perfil";
    }

    // Guardar cambios de perfil
    @PostMapping("/{id}/actualizar")
    public String actualizarPerfil(@PathVariable Long id, @ModelAttribute Perfil perfil) {
        perfil.setId(id);
        perfilService.guardarPerfil(perfil);
        return "redirect:/perfiles/" + id;
    }

    // Eliminar perfil
    @GetMapping("/{id}/eliminar")
    public String eliminarPerfil(@PathVariable Long id) {
        perfilService.eliminarPerfil(id);
        return "redirect:/perfiles/listar";
    }
}
