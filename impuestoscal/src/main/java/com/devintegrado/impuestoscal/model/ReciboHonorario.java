package com.devintegrado.impuestoscal.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "recibos_honorarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReciboHonorario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numeroRecibo;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montoTotal; // Monto bruto

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal retencion; // 8% de retención

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montoNeto; // Monto a cobrar (montoTotal - retencion)

    @Column(length = 500)
    private String descripcionServicio;

    @Column(length = 200)
    private String clienteRazonSocial;

    @Column(length = 11)
    private String clienteRuc;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario emisor;

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
        // Calcular automáticamente la retención del 8%
        if (montoTotal != null && retencion == null) {
            retencion = montoTotal.multiply(new BigDecimal("0.08"));
            montoNeto = montoTotal.subtract(retencion);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
