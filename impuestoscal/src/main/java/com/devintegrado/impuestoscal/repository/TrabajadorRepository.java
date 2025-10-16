package com.devintegrado.impuestoscal.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devintegrado.impuestoscal.model.Trabajador;
import com.devintegrado.impuestoscal.model.Usuario;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {
    
    // Buscar por empresa y activos
    List<Trabajador> findByEmpresaAndActivoTrue(Usuario empresa);
    
    // Buscar por empresa (incluyendo inactivos)
    List<Trabajador> findByEmpresa(Usuario empresa);
    
    // Buscar por DNI
    Optional<Trabajador> findByDniAndActivoTrue(String dni);
    
    // Buscar por DNI y empresa
    Optional<Trabajador> findByDniAndEmpresaAndActivoTrue(String dni, Usuario empresa);
    
    // Contar trabajadores activos
    long countByEmpresaAndActivoTrue(Usuario empresa);
    
    // Calcular suma de sueldos brutos
    @Query("SELECT SUM(t.sueldoBruto) FROM Trabajador t WHERE t.empresa = :empresa AND t.activo = true")
    BigDecimal calcularTotalSueldosBrutos(@Param("empresa") Usuario empresa);
    
    // Calcular suma de aportes pensionarios
    @Query("SELECT SUM(t.aportePensionario) FROM Trabajador t WHERE t.empresa = :empresa AND t.activo = true")
    BigDecimal calcularTotalAportesPensionarios(@Param("empresa") Usuario empresa);
    
    // Calcular suma de retenciones 5ta categoría
    @Query("SELECT SUM(t.retencionQuintaCategoria) FROM Trabajador t WHERE t.empresa = :empresa AND t.activo = true")
    BigDecimal calcularTotalRetenciones(@Param("empresa") Usuario empresa);
    
    // Calcular suma de sueldos netos
    @Query("SELECT SUM(t.sueldoNeto) FROM Trabajador t WHERE t.empresa = :empresa AND t.activo = true")
    BigDecimal calcularTotalSueldosNetos(@Param("empresa") Usuario empresa);
}
