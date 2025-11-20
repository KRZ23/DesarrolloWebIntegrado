package com.devintegrado.impuestoscal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.devintegrado.impuestoscal.model.TipoOperacionIGV;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para encapsular criterios de búsqueda de Operaciones IGV
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperacionIGVFiltro {
    
    private TipoOperacionIGV tipo; // COMPRA o VENTA
    private LocalDate fechaOperacionDesde;
    private LocalDate fechaOperacionHasta;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private String rucTercero;
    private String razonSocialTercero; // Búsqueda parcial
    private String numeroDocumento;
    private Boolean activo;
    private Integer anio;
    private Integer mes;
}
