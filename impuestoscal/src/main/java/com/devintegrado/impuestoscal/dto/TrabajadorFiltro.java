package com.devintegrado.impuestoscal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.devintegrado.impuestoscal.model.TipoRegimenPensionario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para encapsular criterios de búsqueda de Trabajadores
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorFiltro {
    
    private String dni;
    private String nombres; // Búsqueda parcial
    private String apellidoPaterno; // Búsqueda parcial
    private String apellidoMaterno; // Búsqueda parcial
    private TipoRegimenPensionario regimenPensionario;
    private LocalDate fechaIngresoDesde;
    private LocalDate fechaIngresoHasta;
    private BigDecimal sueldoMinimo;
    private BigDecimal sueldoMaximo;
    private Boolean activo;
    private Boolean cesado; // Tienen fechaCese != null
}
