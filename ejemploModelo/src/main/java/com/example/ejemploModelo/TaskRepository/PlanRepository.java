package com.example.ejemploModelo.TaskRepository;

import com.example.ejemploModelo.Models.Plan; // TU clase
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> { }
