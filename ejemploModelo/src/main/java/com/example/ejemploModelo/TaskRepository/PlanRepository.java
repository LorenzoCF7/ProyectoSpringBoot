package com.example.ejemploModelo.TaskRepository;

import com.example.ejemploModelo.Models.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    boolean existsByNombre(String nombre);
}
