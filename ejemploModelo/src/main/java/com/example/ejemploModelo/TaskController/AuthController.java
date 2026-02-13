package com.example.ejemploModelo.TaskController;

import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.Models.Paises;
import com.example.ejemploModelo.Service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "registro", required = false) String registro,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión correctamente");
        }
        if (registro != null) {
            model.addAttribute("mensaje", "Registro exitoso. Inicia sesión con tu email y contraseña");
        }
        return "Views/Auth/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("paises", Paises.values());
        return "Views/Auth/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, 
                                    @RequestParam(value = "confirmPassword") String confirmPassword,
                                    Model model) {
        // Validaciones
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            model.addAttribute("error", "El email es obligatorio");
            model.addAttribute("usuario", usuario);
            model.addAttribute("paises", Paises.values());
            return "Views/Auth/registro";
        }

        if (usuario.getPassword() == null || usuario.getPassword().length() < 4) {
            model.addAttribute("error", "La contraseña debe tener al menos 4 caracteres");
            model.addAttribute("usuario", usuario);
            model.addAttribute("paises", Paises.values());
            return "Views/Auth/registro";
        }

        if (!usuario.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("usuario", usuario);
            model.addAttribute("paises", Paises.values());
            return "Views/Auth/registro";
        }

        if (usuarioService.existeEmail(usuario.getEmail())) {
            model.addAttribute("error", "Ya existe un usuario con ese email");
            model.addAttribute("usuario", usuario);
            model.addAttribute("paises", Paises.values());
            return "Views/Auth/registro";
        }

        usuarioService.registrarUsuario(usuario);
        return "redirect:/login?registro=true";
    }
}
