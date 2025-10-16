package com.devintegrado.impuestoscal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRegisterRequest {
    @NotBlank
    private String rut10;
    @NotBlank
    private String claveSol;
    @NotBlank
    private String tipoPersona; // "NATURAL" o "JURIDICA"
    @NotBlank
    private String nombre; // nombre persona o empresa
}
