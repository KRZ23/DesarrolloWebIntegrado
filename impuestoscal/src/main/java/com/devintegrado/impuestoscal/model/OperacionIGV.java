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
@Table(name = "operaciones_igv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacionIGV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoOperacionIGV tipo; // COMPRA o VENTA

    @Column(nullable = false, length = 20)
    private String numeroDocumento; // Factura, boleta, etc.

    @Column(nullable = false)
    private LocalDate fechaOperacion;

    @Column(nullable = false, length = 200)
    private String razonSocialTercero;

    @Column(length = 11)
    private String rucTercero;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal baseImponible; // Monto sin IGV

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal igv; // 18% del monto

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montoTotal; // baseImponible + igv

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
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
        // Calcular automáticamente el IGV (18%)
        if (baseImponible != null && igv == null) {
            igv = baseImponible.multiply(new BigDecimal("0.18"));
            montoTotal = baseImponible.add(igv);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
