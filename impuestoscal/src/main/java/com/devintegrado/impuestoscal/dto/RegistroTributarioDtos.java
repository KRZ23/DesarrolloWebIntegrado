package com.devintegrado.impuestoscal.dto;

import com.devintegrado.impuestoscal.model.EstadoRegistro;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class RegistroTributarioDtos {
    private RegistroTributarioDtos() {}
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateOrUpdateRequest {
        @NotBlank
        @Size(max = 80)
        private String tipoImpuesto;

        @NotNull
        @DecimalMin(value = "0.00", inclusive = false)
        private BigDecimal monto;

        @NotNull
        private LocalDate fechaVencimiento;

        private EstadoRegistro estado; // opcional al crear; por defecto PENDIENTE
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String tipoImpuesto;
        private BigDecimal monto;
        private LocalDate fechaVencimiento;
        private EstadoRegistro estado;
    }
}


