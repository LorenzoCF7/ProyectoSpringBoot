package com.example.ejemploModelo.TaskRepository;

import com.example.ejemploModelo.Models.Factura; // TU clase
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> { }