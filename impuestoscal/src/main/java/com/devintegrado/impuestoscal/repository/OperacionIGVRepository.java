package com.devintegrado.impuestoscal.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devintegrado.impuestoscal.model.OperacionIGV;
import com.devintegrado.impuestoscal.model.TipoOperacionIGV;
import com.devintegrado.impuestoscal.model.Usuario;

public interface OperacionIGVRepository extends JpaRepository<OperacionIGV, Long> {
    
    // Buscar por empresa y activos
    List<OperacionIGV> findByEmpresaAndActivoTrue(Usuario empresa);
    
    // Buscar por empresa (incluyendo inactivos)
    List<OperacionIGV> findByEmpresa(Usuario empresa);
    
    // Buscar por tipo de operación
    List<OperacionIGV> findByEmpresaAndTipoAndActivoTrue(Usuario empresa, TipoOperacionIGV tipo);
    
    // Buscar por rango de fechas
    List<OperacionIGV> findByEmpresaAndFechaOperacionBetweenAndActivoTrue(
        Usuario empresa, LocalDate fechaInicio, LocalDate fechaFin);
    
    // Buscar por mes y año
    @Query("SELECT o FROM OperacionIGV o WHERE o.empresa = :empresa " +
           "AND MONTH(o.fechaOperacion) = :mes AND YEAR(o.fechaOperacion) = :anio AND o.activo = true")
    List<OperacionIGV> findByEmpresaAndMesAnio(
        @Param("empresa") Usuario empresa, @Param("mes") int mes, @Param("anio") int anio);
    
    // Calcular total IGV de ventas por periodo
    @Query("SELECT SUM(o.igv) FROM OperacionIGV o WHERE o.empresa = :empresa " +
           "AND o.tipo = 'VENTA' AND MONTH(o.fechaOperacion) = :mes " +
           "AND YEAR(o.fechaOperacion) = :anio AND o.activo = true")
    BigDecimal calcularIGVVentasMensual(
        @Param("empresa") Usuario empresa, @Param("mes") int mes, @Param("anio") int anio);
    
    // Calcular total IGV de compras por periodo
    @Query("SELECT SUM(o.igv) FROM OperacionIGV o WHERE o.empresa = :empresa " +
           "AND o.tipo = 'COMPRA' AND MONTH(o.fechaOperacion) = :mes " +
           "AND YEAR(o.fechaOperacion) = :anio AND o.activo = true")
    BigDecimal calcularIGVComprasMensual(
        @Param("empresa") Usuario empresa, @Param("mes") int mes, @Param("anio") int anio);
    
    // Calcular base imponible total de ventas
    @Query("SELECT SUM(o.baseImponible) FROM OperacionIGV o WHERE o.empresa = :empresa " +
           "AND o.tipo = 'VENTA' AND MONTH(o.fechaOperacion) = :mes " +
           "AND YEAR(o.fechaOperacion) = :anio AND o.activo = true")
    BigDecimal calcularBaseImponibleVentasMensual(
        @Param("empresa") Usuario empresa, @Param("mes") int mes, @Param("anio") int anio);
    
    // Calcular base imponible total de compras
    @Query("SELECT SUM(o.baseImponible) FROM OperacionIGV o WHERE o.empresa = :empresa " +
           "AND o.tipo = 'COMPRA' AND MONTH(o.fechaOperacion) = :mes " +
           "AND YEAR(o.fechaOperacion) = :anio AND o.activo = true")
    BigDecimal calcularBaseImponibleComprasMensual(
        @Param("empresa") Usuario empresa, @Param("mes") int mes, @Param("anio") int anio);
}
