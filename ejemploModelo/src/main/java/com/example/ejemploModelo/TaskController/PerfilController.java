package com.example.ejemploModelo.TaskController;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ejemploModelo.Models.Perfil;
import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.Service.AuditoriaService;
import com.example.ejemploModelo.Service.PerfilService;
import com.example.ejemploModelo.Service.UsuarioService;

@Controller
@RequestMapping("/perfiles")
public class PerfilController {
    private final PerfilService perfilService;
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    public PerfilController(PerfilService perfilService, UsuarioService usuarioService, AuditoriaService auditoriaService) {
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    private Usuario getUsuarioActual(Authentication auth) {
        return usuarioService.obtenerPorEmail(auth.getName()).orElse(null);
    }

    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Mostrar formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model, Authentication auth) {
        model.addAttribute("perfil", new Perfil());
        if (esAdmin(auth)) {
            model.addAttribute("usuarios", usuarioService.listarUsuarios());
        }
        return "Views/Perfil/formulario_perfil";
    }

    // Guardar perfil
    @PostMapping("/guardar")
    public String guardarPerfil(@ModelAttribute Perfil perfil, Authentication auth) {
        Usuario usuario = getUsuarioActual(auth);
        if (!esAdmin(auth)) {
            perfil.setUsuario(usuario);
        }
        perfilService.guardarPerfil(perfil);
        auditoriaService.registrar("CREAR", "Perfil", perfil.getId(), 
            "Perfil creado: " + perfil.getNombre() + " " + perfil.getApellido());
        return "redirect:/perfiles/listar";
    }

    // Listar perfiles (solo los del usuario logueado, o todos si es admin)
    @GetMapping("/listar")
    public String listarPerfiles(Model model, Authentication auth) {
        if (esAdmin(auth)) {
            model.addAttribute("perfiles", perfilService.listarPerfiles());
        } else {
            Usuario usuario = getUsuarioActual(auth);
            model.addAttribute("perfiles", perfilService.listarPerfilesPorUsuario(usuario));
        }
        return "Views/Perfil/lista_perfiles";
    }

    // Mostrar detalle del perfil
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model, Authentication auth) {
        var perfil = perfilService.obtenerPorId(id);
        if (perfil.isEmpty()) {
            return "redirect:/perfiles/listar";
        }
        // Verificar que el perfil pertenece al usuario si no es admin
        if (!esAdmin(auth)) {
            Usuario usuario = getUsuarioActual(auth);
            if (!perfil.get().getUsuario().getId().equals(usuario.getId())) {
                return "redirect:/perfiles/listar";
            }
        }
        model.addAttribute("perfil", perfil.get());
        return "Views/Perfil/detalle_perfil";
    }

    // Mostrar formulario de edición
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, Authentication auth) {
        var perfil = perfilService.obtenerPorId(id);
        if (perfil.isEmpty()) {
            return "redirect:/perfiles/listar";
        }
        if (!esAdmin(auth)) {
            Usuario usuario = getUsuarioActual(auth);
            if (!perfil.get().getUsuario().getId().equals(usuario.getId())) {
                return "redirect:/perfiles/listar";
            }
        }
        model.addAttribute("perfil", perfil.get());
        if (esAdmin(auth)) {
            model.addAttribute("usuarios", usuarioService.listarUsuarios());
        }
        return "Views/Perfil/editar_perfil";
    }

    // Guardar cambios de perfil
    @PostMapping("/{id}/actualizar")
    public String actualizarPerfil(@PathVariable Long id, @ModelAttribute Perfil perfil, Authentication auth) {
        if (!esAdmin(auth)) {
            Usuario usuario = getUsuarioActual(auth);
            var existente = perfilService.obtenerPorId(id);
            if (existente.isEmpty() || !existente.get().getUsuario().getId().equals(usuario.getId())) {
                return "redirect:/perfiles/listar";
            }
            perfil.setUsuario(usuario);
        }
        perfil.setId(id);
        perfilService.guardarPerfil(perfil);
        auditoriaService.registrar("EDITAR", "Perfil", id, 
            "Perfil editado: " + perfil.getNombre() + " " + perfil.getApellido());
        return "redirect:/perfiles/" + id;
    }

    // Eliminar perfil
    @GetMapping("/{id}/eliminar")
    public String eliminarPerfil(@PathVariable Long id, Authentication auth) {
        if (!esAdmin(auth)) {
            Usuario usuario = getUsuarioActual(auth);
            var perfil = perfilService.obtenerPorId(id);
            if (perfil.isEmpty() || !perfil.get().getUsuario().getId().equals(usuario.getId())) {
                return "redirect:/perfiles/listar";
            }
        }
        var perfil = perfilService.obtenerPorId(id);
        perfilService.eliminarPerfil(id);
        auditoriaService.registrar("ELIMINAR", "Perfil", id, 
            "Perfil eliminado" + (perfil.isPresent() ? ": " + perfil.get().getNombre() : ""));
        return "redirect:/perfiles/listar";
    }
}
