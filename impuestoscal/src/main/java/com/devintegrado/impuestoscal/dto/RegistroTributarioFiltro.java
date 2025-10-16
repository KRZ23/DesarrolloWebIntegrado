package com.devintegrado.impuestoscal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.devintegrado.impuestoscal.model.EstadoRegistro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para encapsular criterios de búsqueda de Registros Tributarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroTributarioFiltro {
    
    private String tipoImpuesto; // Búsqueda parcial
    private EstadoRegistro estado;
    private LocalDate fechaVencimientoDesde;
    private LocalDate fechaVencimientoHasta;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private Boolean activo;
    private Boolean vencido; // Fecha vencimiento < hoy y estado PENDIENTE
    private Integer diasProximoVencimiento; // Próximos X días
}
