package com.devintegrado.impuestoscal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para encapsular criterios de búsqueda de Recibos por Honorarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReciboHonorarioFiltro {
    
    private LocalDate fechaEmisionDesde;
    private LocalDate fechaEmisionHasta;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private String clienteRuc;
    private String clienteRazonSocial; // Búsqueda parcial
    private Boolean activo;
    private Integer anio;
    private Integer mes;
}
