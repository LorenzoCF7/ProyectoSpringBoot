package com.example.ejemploModelo.TaskController;

import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.Service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CP-AUTH-xx: Pruebas unitarias para AuthController.
 * Verifica validaciones del flujo de registro y login directamente
 * invocando los métodos del controlador (sin contexto Spring MVC completo).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CP-AUTH: Controlador de Autenticación (Unit)")
class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ExtendedModelMap();
    }

    // ─── CP-AUTH-01: GET /login ──────────────────────────────────────────────

    @Test
    @DisplayName("CP-AUTH-01a: GET /login sin parámetros retorna vista login")
    void testGetLogin_sinParametros() {
        String vista = authController.mostrarLogin(null, null, null, model);
        assertEquals("Views/Auth/login", vista);
        assertFalse(model.containsAttribute("error"));
        assertFalse(model.containsAttribute("mensaje"));
    }

    @Test
    @DisplayName("CP-AUTH-01b: GET /login?error agrega atributo error al modelo")
    void testGetLogin_conError() {
        String vista = authController.mostrarLogin("true", null, null, model);
        assertEquals("Views/Auth/login", vista);
        assertTrue(model.containsAttribute("error"));
    }

    @Test
    @DisplayName("CP-AUTH-01c: GET /login?logout agrega mensaje de cierre al modelo")
    void testGetLogin_conLogout() {
        String vista = authController.mostrarLogin(null, "true", null, model);
        assertEquals("Views/Auth/login", vista);
        assertTrue(model.containsAttribute("mensaje"));
    }

    @Test
    @DisplayName("CP-AUTH-01d: GET /login?registro agrega mensaje de registro exitoso")
    void testGetLogin_conRegistroExitoso() {
        String vista = authController.mostrarLogin(null, null, "true", model);
        assertEquals("Views/Auth/login", vista);
        assertTrue(model.containsAttribute("mensaje"));
    }

    // ─── CP-AUTH-02: GET /registro ───────────────────────────────────────────

    @Test
    @DisplayName("CP-AUTH-02: GET /registro retorna vista registro con atributos")
    void testGetRegistro() {
        String vista = authController.mostrarRegistro(model);
        assertEquals("Views/Auth/registro", vista);
        assertTrue(model.containsAttribute("usuario"));
        assertTrue(model.containsAttribute("paises"));
    }

    // ─── CP-AUTH-03: POST /registro – validaciones ───────────────────────────

    @Test
    @DisplayName("CP-AUTH-03a: POST registro con email null/vacío muestra error")
    void testPostRegistro_emailVacio() {
        Usuario u = new Usuario();
        u.setEmail("");
        u.setPassword("securePass");

        String vista = authController.registrarUsuario(u, "securePass", model);

        assertEquals("Views/Auth/registro", vista);
        assertTrue(model.containsAttribute("error"));
        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("CP-AUTH-03b: POST registro con contraseña corta (< 4) muestra error")
    void testPostRegistro_contrasenaDemadiadoCorta() {
        Usuario u = new Usuario();
        u.setEmail("test@test.com");
        u.setPassword("123");

        String vista = authController.registrarUsuario(u, "123", model);

        assertEquals("Views/Auth/registro", vista);
        assertTrue(model.containsAttribute("error"));
        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("CP-AUTH-03c: POST registro cuando contraseñas no coinciden muestra error")
    void testPostRegistro_contrasenasNoCoinciden() {
        Usuario u = new Usuario();
        u.setEmail("test@test.com");
        u.setPassword("correcta123");

        String vista = authController.registrarUsuario(u, "distinta456", model);

        assertEquals("Views/Auth/registro", vista);
        assertTrue(model.containsAttribute("error"));
        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("CP-AUTH-03d: POST registro con email duplicado muestra error")
    void testPostRegistro_emailDuplicado() {
        when(usuarioService.existeEmail("existente@test.com")).thenReturn(true);

        Usuario u = new Usuario();
        u.setEmail("existente@test.com");
        u.setPassword("password123");

        String vista = authController.registrarUsuario(u, "password123", model);

        assertEquals("Views/Auth/registro", vista);
        assertTrue(model.containsAttribute("error"));
        verify(usuarioService, never()).registrarUsuario(any());
    }

    @Test
    @DisplayName("CP-AUTH-03e: POST registro exitoso redirige a /login?registro=true")
    void testPostRegistro_exitoso() {
        when(usuarioService.existeEmail("nuevo@test.com")).thenReturn(false);
        when(usuarioService.registrarUsuario(any(Usuario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Usuario u = new Usuario();
        u.setEmail("nuevo@test.com");
        u.setPassword("securePass123");

        String vista = authController.registrarUsuario(u, "securePass123", model);

        assertEquals("redirect:/login?registro=true", vista);
        verify(usuarioService).registrarUsuario(u);
    }
}

