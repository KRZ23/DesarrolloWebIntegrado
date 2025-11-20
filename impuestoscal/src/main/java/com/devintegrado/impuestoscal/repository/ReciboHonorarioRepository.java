package com.devintegrado.impuestoscal.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devintegrado.impuestoscal.model.ReciboHonorario;
import com.devintegrado.impuestoscal.model.Usuario;

public interface ReciboHonorarioRepository extends JpaRepository<ReciboHonorario, Long> {
    
    // Buscar por emisor y activos
    List<ReciboHonorario> findByEmisorAndActivoTrue(Usuario emisor);
    
    // Buscar por emisor (incluyendo inactivos)
    List<ReciboHonorario> findByEmisor(Usuario emisor);
    
    // Buscar por número de recibo
    Optional<ReciboHonorario> findByNumeroReciboAndActivoTrue(String numeroRecibo);
    
    // Buscar recibos por rango de fechas
    List<ReciboHonorario> findByEmisorAndFechaEmisionBetweenAndActivoTrue(
        Usuario emisor, LocalDate fechaInicio, LocalDate fechaFin);
    
    // Calcular total de retenciones por periodo
    @Query("SELECT SUM(r.retencion) FROM ReciboHonorario r WHERE r.emisor = :emisor " +
           "AND YEAR(r.fechaEmision) = :anio AND r.activo = true")
    BigDecimal calcularRetencionAnual(@Param("emisor") Usuario emisor, @Param("anio") int anio);
    
    // Calcular total de ingresos por periodo
    @Query("SELECT SUM(r.montoTotal) FROM ReciboHonorario r WHERE r.emisor = :emisor " +
           "AND YEAR(r.fechaEmision) = :anio AND r.activo = true")
    BigDecimal calcularIngresosAnuales(@Param("emisor") Usuario emisor, @Param("anio") int anio);
    
    // Buscar por mes y año
    @Query("SELECT r FROM ReciboHonorario r WHERE r.emisor = :emisor " +
           "AND MONTH(r.fechaEmision) = :mes AND YEAR(r.fechaEmision) = :anio AND r.activo = true")
    List<ReciboHonorario> findByEmisorAndMesAnio(
        @Param("emisor") Usuario emisor, @Param("mes") int mes, @Param("anio") int anio);
}
