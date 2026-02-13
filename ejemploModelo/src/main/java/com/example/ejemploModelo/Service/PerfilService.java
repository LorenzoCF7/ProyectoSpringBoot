package com.example.ejemploModelo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ejemploModelo.Models.Perfil;
import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.TaskRepository.PerfilRepository;

@Service
public class PerfilService {
    private final PerfilRepository serviceRepository;

    public PerfilService(PerfilRepository perfilRepository) {
        this.serviceRepository = perfilRepository;
    }

    public Perfil guardarPerfil(Perfil perfil) {
        return serviceRepository.save(perfil);
    }

    public List<Perfil> listarPerfiles() {
        return serviceRepository.findAll();
    }

    public List<Perfil> listarPerfilesPorUsuario(Usuario usuario) {
        return serviceRepository.findByUsuario(usuario);
    }

    public Optional<Perfil> obtenerPorId(Long id) {
        return serviceRepository.findById(id);
    }

    public void eliminarPerfil(Long id) {
        serviceRepository.deleteById(id);
    }
}
