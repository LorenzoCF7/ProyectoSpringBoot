package com.example.ejemploModelo.Service;

import com.example.ejemploModelo.Models.Paises;
import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.TaskRepository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de Usuarios")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = new Usuario();
        usuarioBase.setId(1L);
        usuarioBase.setEmail("test@test.com");
        usuarioBase.setPassword("plain1234");
        usuarioBase.setPais(Paises.ES);
    }

    //  Registro 

    @Test
    @DisplayName("La contraseña se codifica al registrar")
    void testRegistrarUsuario_passwordEncoded() {
        when(passwordEncoder.encode("plain1234")).thenReturn("HASH_ENCODED");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.registrarUsuario(usuarioBase);

        assertEquals("HASH_ENCODED", resultado.getPassword());
        verify(passwordEncoder).encode("plain1234");
    }

    @Test
    @DisplayName("El usuario queda activo tras registrarse")
    void testRegistrarUsuario_activoTrue() {
        when(passwordEncoder.encode(anyString())).thenReturn("HASH");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.registrarUsuario(usuarioBase);

        assertTrue(resultado.isActivo());
    }

    @Test
    @DisplayName("Se asigna ROLE_USER cuando el rol está vacío")
    void testRegistrarUsuario_roleUserPorDefecto() {
        when(passwordEncoder.encode(anyString())).thenReturn("HASH");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        usuarioBase.setRol(null);
        Usuario resultado = usuarioService.registrarUsuario(usuarioBase);

        assertEquals("ROLE_USER", resultado.getRol());
    }

    @Test
    @DisplayName("El rol ROLE_ADMIN se conserva si ya está asignado")
    void testRegistrarUsuario_conservaRolAdmin() {
        usuarioBase.setRol("ROLE_ADMIN");
        when(passwordEncoder.encode(anyString())).thenReturn("HASH");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.registrarUsuario(usuarioBase);

        assertEquals("ROLE_ADMIN", resultado.getRol());
    }

    //  Existencia de email

    @Test
    @DisplayName("existeEmail devuelve true cuando el email ya existe")
    void testExisteEmail_true() {
        when(usuarioRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertTrue(usuarioService.existeEmail("test@test.com"));
    }

    @Test
    @DisplayName("existeEmail devuelve false cuando el email no existe")
    void testExisteEmail_false() {
        when(usuarioRepository.existsByEmail("nuevo@test.com")).thenReturn(false);

        assertFalse(usuarioService.existeEmail("nuevo@test.com"));
    }

    //  Búsqueda

    @Test
    @DisplayName("obtenerPorId retorna el usuario cuando existe")
    void testObtenerPorId_presente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        Optional<Usuario> resultado = usuarioService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("test@test.com", resultado.get().getEmail());
    }

    @Test
    @DisplayName("obtenerPorId retorna vacío cuando no existe")
    void testObtenerPorId_ausente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("obtenerPorEmail retorna el usuario cuando existe")
    void testObtenerPorEmail_presente() {
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuarioBase));

        Optional<Usuario> resultado = usuarioService.obtenerPorEmail("test@test.com");

        assertTrue(resultado.isPresent());
    }

    //  Listar y eliminar

    @Test
    @DisplayName("listarUsuarios devuelve todos los usuarios")
    void testListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioBase));

        List<Usuario> lista = usuarioService.listarUsuarios();

        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("eliminarUsuario invoca deleteById")
    void testEliminarUsuario() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository).deleteById(1L);
    }
}
