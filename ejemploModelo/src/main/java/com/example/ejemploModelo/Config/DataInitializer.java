package com.example.ejemploModelo.Config;

import com.example.ejemploModelo.Models.Paises;
import com.example.ejemploModelo.Models.Plan;
import com.example.ejemploModelo.Models.Usuario;
import com.example.ejemploModelo.TaskRepository.PlanRepository;
import com.example.ejemploModelo.TaskRepository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PlanRepository planRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear usuario admin si no existe
            if (!usuarioRepository.existsByEmail("admin@admin.com")) {
                Usuario admin = new Usuario();
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setPais(Paises.ES);
                admin.setActivo(true);
                admin.setRol("ROLE_ADMIN");
                usuarioRepository.save(admin);
                System.out.println(">>> Usuario ADMIN creado: admin@admin.com / admin");
            }

            // Crear los 3 planes si no existen
            if (!planRepository.existsByNombre("Basic")) {
                Plan basic = new Plan();
                basic.setNombre("Basic");
                basic.setPrecio(9.99);
                basic.setDuracionMeses(1);
                basic.setDescripcion("Plan básico con acceso limitado. Ideal para usuarios individuales.");
                planRepository.save(basic);
                System.out.println(">>> Plan BASIC creado");
            }

            if (!planRepository.existsByNombre("Premium")) {
                Plan premium = new Plan();
                premium.setNombre("Premium");
                premium.setPrecio(19.99);
                premium.setDuracionMeses(6);
                premium.setDescripcion("Plan premium con acceso completo y soporte prioritario.");
                planRepository.save(premium);
                System.out.println(">>> Plan PREMIUM creado");
            }

            if (!planRepository.existsByNombre("Enterprise")) {
                Plan enterprise = new Plan();
                enterprise.setNombre("Enterprise");
                enterprise.setPrecio(49.99);
                enterprise.setDuracionMeses(12);
                enterprise.setDescripcion("Plan empresarial con acceso ilimitado, soporte 24/7 y funciones avanzadas.");
                planRepository.save(enterprise);
                System.out.println(">>> Plan ENTERPRISE creado");
            }
        };
    }
}
