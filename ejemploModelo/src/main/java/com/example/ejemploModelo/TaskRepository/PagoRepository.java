package com.example.ejemploModelo.TaskRepository;

import com.example.ejemploModelo.Models.Pago;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}