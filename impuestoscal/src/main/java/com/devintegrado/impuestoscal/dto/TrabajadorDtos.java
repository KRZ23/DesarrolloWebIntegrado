package com.devintegrado.impuestoscal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.devintegrado.impuestoscal.model.TipoRegimenPensionario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TrabajadorDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
        private String dni;

        @NotBlank(message = "Los nombres son obligatorios")
        private String nombres;

        @NotBlank(message = "El apellido paterno es obligatorio")
        private String apellidoPaterno;

        @NotBlank(message = "El apellido materno es obligatorio")
        private String apellidoMaterno;

        @Email(message = "El email debe ser válido")
        private String email;

        private String telefono;
        private String direccion;

        @NotNull(message = "La fecha de ingreso es obligatoria")
        private LocalDate fechaIngreso;

        @NotNull(message = "El sueldo bruto es obligatorio")
        @Positive(message = "El sueldo debe ser positivo")
        private BigDecimal sueldoBruto;

        @NotNull(message = "El régimen pensionario es obligatorio")
        private TipoRegimenPensionario regimenPensionario;

        private String afpNombre; // Solo si es AFP
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String nombres;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private String email;
        private String telefono;
        private String direccion;
        private LocalDate fechaCese;
        private BigDecimal sueldoBruto;
        private TipoRegimenPensionario regimenPensionario;
        private String afpNombre;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String dni;
        private String nombreCompleto;
        private String nombres;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private String email;
        private String telefono;
        private String direccion;
        private LocalDate fechaIngreso;
        private LocalDate fechaCese;
        private BigDecimal sueldoBruto;
        private TipoRegimenPensionario regimenPensionario;
        private String afpNombre;
        private BigDecimal aportePensionario;
        private BigDecimal retencionQuintaCategoria;
        private BigDecimal sueldoNeto;
        private Boolean activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenPlanilla {
        private Integer mes;
        private Integer anio;
        private Integer totalTrabajadores;
        private BigDecimal totalSueldosBrutos;
        private BigDecimal totalAportesPensionarios;
        private BigDecimal totalRetenciones;
        private BigDecimal totalSueldosNetos;
        private BigDecimal essaludEmpleador; // 9% del total de sueldos
    }
}
