/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ejemploModelo.TaskController;

import com.example.ejemploModelo.Models.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.ejemploModelo.Service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Mostrar formulario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "Views/Usuario/formulario_usuario";
    }

    // Guardar usuario
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.guardarUsuario(usuario);
        return "redirect:/usuarios/listar";
    }

    // Listar usuarios
    @GetMapping("/listar")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "Views/Usuario/lista_usuarios";
    }

    // Mostrar detalle del usuario
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model) {
        var usuario = usuarioService.obtenerPorId(id);
        if (usuario.isEmpty()) {
            return "redirect:/usuarios/listar";
        }
        model.addAttribute("usuario", usuario.get());
        return "Views/Usuario/detalle_usuario";
    }

    // Mostrar formulario de edición
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        var usuario = usuarioService.obtenerPorId(id);
        if (usuario.isEmpty()) {
            return "redirect:/usuarios/listar";
        }
        model.addAttribute("usuario", usuario.get());
        return "Views/Usuario/editar_usuario";
    }

    // Guardar cambios de usuario
    @PostMapping("/{id}/actualizar")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute Usuario usuario) {
        usuario.setId(id);
        usuarioService.guardarUsuario(usuario);
        return "redirect:/usuarios/" + id;
    }

    // Eliminar usuario
    @GetMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios/listar";
    }
}
