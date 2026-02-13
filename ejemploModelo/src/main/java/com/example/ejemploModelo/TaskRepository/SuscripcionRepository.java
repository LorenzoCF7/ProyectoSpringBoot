package com.example.ejemploModelo.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.ejemploModelo.Models.Suscripcion;
import com.example.ejemploModelo.Models.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
	List<Suscripcion> findByRenovacionAutomaticaTrueAndFechaFinLessThanEqual(LocalDate fecha);
	Optional<Suscripcion> findByUsuario(Usuario usuario);
}