package com.devintegrado.impuestoscal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReciboHonorarioDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "El número de recibo es obligatorio")
        private String numeroRecibo;

        @NotNull(message = "La fecha de emisión es obligatoria")
        private LocalDate fechaEmision;

        @NotNull(message = "El monto total es obligatorio")
        @Positive(message = "El monto debe ser positivo")
        private BigDecimal montoTotal;

        private String descripcionServicio;

        @NotBlank(message = "La razón social del cliente es obligatoria")
        private String clienteRazonSocial;

        private String clienteRuc;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private LocalDate fechaEmision;
        private BigDecimal montoTotal;
        private String descripcionServicio;
        private String clienteRazonSocial;
        private String clienteRuc;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String numeroRecibo;
        private LocalDate fechaEmision;
        private BigDecimal montoTotal;
        private BigDecimal retencion;
        private BigDecimal montoNeto;
        private String descripcionServicio;
        private String clienteRazonSocial;
        private String clienteRuc;
        private Boolean activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
    }
}
