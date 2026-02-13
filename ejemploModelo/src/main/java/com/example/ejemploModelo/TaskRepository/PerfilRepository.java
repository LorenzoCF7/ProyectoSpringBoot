package com.example.ejemploModelo.TaskRepository;

import com.example.ejemploModelo.Models.Perfil;
import com.example.ejemploModelo.Models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    List<Perfil> findByUsuario(Usuario usuario);
}