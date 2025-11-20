package com.devintegrado.impuestoscal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.devintegrado.impuestoscal.model.TipoOperacionIGV;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OperacionIGVDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "El tipo de operación es obligatorio")
        private TipoOperacionIGV tipo;

        @NotBlank(message = "El número de documento es obligatorio")
        private String numeroDocumento;

        @NotNull(message = "La fecha de operación es obligatoria")
        private LocalDate fechaOperacion;

        @NotBlank(message = "La razón social es obligatoria")
        private String razonSocialTercero;

        private String rucTercero;

        @NotNull(message = "La base imponible es obligatoria")
        @Positive(message = "La base imponible debe ser positiva")
        private BigDecimal baseImponible;

        private String descripcion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private TipoOperacionIGV tipo;
        private String numeroDocumento;
        private LocalDate fechaOperacion;
        private String razonSocialTercero;
        private String rucTercero;
        private BigDecimal baseImponible;
        private String descripcion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private TipoOperacionIGV tipo;
        private String numeroDocumento;
        private LocalDate fechaOperacion;
        private String razonSocialTercero;
        private String rucTercero;
        private BigDecimal baseImponible;
        private BigDecimal igv;
        private BigDecimal montoTotal;
        private String descripcion;
        private Boolean activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenIGV {
        private Integer mes;
        private Integer anio;
        private BigDecimal totalVentas;
        private BigDecimal igvVentas;
        private BigDecimal totalCompras;
        private BigDecimal igvCompras;
        private BigDecimal igvAPagar; // IGV Ventas - IGV Compras
    }
}
