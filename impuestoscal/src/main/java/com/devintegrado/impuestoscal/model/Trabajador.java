package com.devintegrado.impuestoscal.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trabajadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(nullable = false, length = 100)
    private String apellidoMaterno;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @Column
    private LocalDate fechaCese;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal sueldoBruto; // Sueldo mensual

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoRegimenPensionario regimenPensionario;

    @Column(length = 50)
    private String afpNombre; // Solo si es AFP

    // Cálculos automáticos (se pueden calcular en el servicio)
    @Column(precision = 15, scale = 2)
    private BigDecimal aportePensionario; // 13% ONP o 10-13% AFP

    @Column(precision = 15, scale = 2)
    private BigDecimal retencionQuintaCategoria; // Impuesto a la renta 5ta

    @Column(precision = 15, scale = 2)
    private BigDecimal sueldoNeto; // Sueldo después de descuentos

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Usuario empresa;

    // Auditoría
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Método auxiliar para obtener nombre completo
    public String getNombreCompleto() {
        return String.format("%s %s %s", nombres, apellidoPaterno, apellidoMaterno);
    }
}
