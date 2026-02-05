package com.example.ejemploModelo.TaskRepository;

import com.example.ejemploModelo.Models.Perfil; // TU clase
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Long> { }