package com.example.ejemploModelo.TaskRepository;

import com.example.ejemploModelo.Models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    // Filtrar por fecha exacta
    List<Factura> findByFechaEmision(LocalDate fechaEmision);
    
    // Filtrar por rango de fechas
    List<Factura> findByFechaEmisionBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    // Filtrar por fecha mayor o igual
    List<Factura> findByFechaEmisionGreaterThanEqual(LocalDate fechaInicio);
    
    // Filtrar por fecha menor o igual
    List<Factura> findByFechaEmisionLessThanEqual(LocalDate fechaFin);
    
    // Filtrar por monto mayor o igual
    List<Factura> findByMontoGreaterThanEqual(Double montoMin);
    
    // Filtrar por monto menor o igual
    List<Factura> findByMontoLessThanEqual(Double montoMax);
    
    // Filtrar por rango de montos
    List<Factura> findByMontoBetween(Double montoMin, Double montoMax);
    
    // Query personalizada para filtrado avanzado
    @Query("SELECT f FROM Factura f WHERE " +
           "(:fechaInicio IS NULL OR f.fechaEmision >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR f.fechaEmision <= :fechaFin) " +
           "ORDER BY f.fechaEmision DESC")
    List<Factura> findByFechaRange(@Param("fechaInicio") LocalDate fechaInicio, 
                                    @Param("fechaFin") LocalDate fechaFin);
    
    // Query personalizada con filtros de fecha y monto combinados
    @Query("SELECT f FROM Factura f WHERE " +
           "(:fechaInicio IS NULL OR f.fechaEmision >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR f.fechaEmision <= :fechaFin) AND " +
           "(:montoMin IS NULL OR f.monto >= :montoMin) AND " +
           "(:montoMax IS NULL OR f.monto <= :montoMax) " +
           "ORDER BY f.fechaEmision DESC")
    List<Factura> findByFechaYMontoRange(@Param("fechaInicio") LocalDate fechaInicio,
                                          @Param("fechaFin") LocalDate fechaFin,
                                          @Param("montoMin") Double montoMin,
                                          @Param("montoMax") Double montoMax);
}