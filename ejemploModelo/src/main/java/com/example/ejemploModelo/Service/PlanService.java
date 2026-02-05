package com.example.ejemploModelo.Service;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ejemploModelo.Models.Plan;
import com.example.ejemploModelo.TaskRepository.PlanRepository;

@Service
public class PlanService {
    private final PlanRepository serviceRepository;

    public PlanService(PlanRepository planRepository) {
        this.serviceRepository = planRepository;
    }

    public Plan guardarPlan(Plan plan) {
        return serviceRepository.save(plan);
    }

    public List<Plan> listarPlanes() {
        return serviceRepository.findAll();
    }

    public Optional<Plan> obtenerPorId(Long id) {
        return serviceRepository.findById(id);
    }

    public void eliminarPlan(Long id) {
        serviceRepository.deleteById(id);
    }
}