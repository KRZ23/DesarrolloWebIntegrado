package com.devintegrado.impuestoscal.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devintegrado.impuestoscal.dto.OperacionIGVDtos;
import com.devintegrado.impuestoscal.model.OperacionIGV;
import com.devintegrado.impuestoscal.model.TipoOperacionIGV;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.OperacionIGVRepository;

@Service
public class OperacionIGVService {
    
    private final OperacionIGVRepository repository;
    
    public OperacionIGVService(OperacionIGVRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public List<OperacionIGVDtos.Response> listarPorEmpresa(Usuario empresa) {
        return repository.findByEmpresaAndActivoTrue(empresa).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OperacionIGVDtos.Response> listarPorTipo(Usuario empresa, TipoOperacionIGV tipo) {
        return repository.findByEmpresaAndTipoAndActivoTrue(empresa, tipo).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public OperacionIGVDtos.Response buscarPorId(Long id, Usuario empresa) {
        OperacionIGV operacion = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operación no encontrada"));
        
        if (!operacion.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("No autorizado para ver esta operación");
        }
        
        return toDto(operacion);
    }
    
    @Transactional
    public OperacionIGVDtos.Response crear(OperacionIGVDtos.CreateRequest request, Usuario empresa) {
        // Calcular IGV (18%)
        BigDecimal igv = request.getBaseImponible().multiply(new BigDecimal("0.18"));
        BigDecimal montoTotal = request.getBaseImponible().add(igv);
        
        OperacionIGV operacion = OperacionIGV.builder()
                .tipo(request.getTipo())
                .numeroDocumento(request.getNumeroDocumento())
                .fechaOperacion(request.getFechaOperacion())
                .razonSocialTercero(request.getRazonSocialTercero())
                .rucTercero(request.getRucTercero())
                .baseImponible(request.getBaseImponible())
                .igv(igv)
                .montoTotal(montoTotal)
                .descripcion(request.getDescripcion())
                .empresa(empresa)
                .activo(true)
                .build();
        
        operacion = repository.save(operacion);
        return toDto(operacion);
    }
    
    @Transactional
    public OperacionIGVDtos.Response actualizar(Long id, OperacionIGVDtos.UpdateRequest request, Usuario empresa) {
        OperacionIGV operacion = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operación no encontrada"));
        
        if (!operacion.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("No autorizado para modificar esta operación");
        }
        
        if (!operacion.getActivo()) {
            throw new IllegalArgumentException("No se puede modificar una operación inactiva");
        }
        
        // Actualizar campos
        if (request.getTipo() != null) {
            operacion.setTipo(request.getTipo());
        }
        if (request.getNumeroDocumento() != null) {
            operacion.setNumeroDocumento(request.getNumeroDocumento());
        }
        if (request.getFechaOperacion() != null) {
            operacion.setFechaOperacion(request.getFechaOperacion());
        }
        if (request.getRazonSocialTercero() != null) {
            operacion.setRazonSocialTercero(request.getRazonSocialTercero());
        }
        if (request.getRucTercero() != null) {
            operacion.setRucTercero(request.getRucTercero());
        }
        if (request.getBaseImponible() != null) {
            operacion.setBaseImponible(request.getBaseImponible());
            // Recalcular IGV
            BigDecimal igv = request.getBaseImponible().multiply(new BigDecimal("0.18"));
            operacion.setIgv(igv);
            operacion.setMontoTotal(request.getBaseImponible().add(igv));
        }
        if (request.getDescripcion() != null) {
            operacion.setDescripcion(request.getDescripcion());
        }
        
        operacion = repository.save(operacion);
        return toDto(operacion);
    }
    
    @Transactional
    public void eliminar(Long id, Usuario empresa) {
        OperacionIGV operacion = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operación no encontrada"));
        
        if (!operacion.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("No autorizado para eliminar esta operación");
        }
        
        // Eliminación lógica: cambiar estado a inactivo
        operacion.setActivo(false);
        repository.save(operacion);
    }
    
    @Transactional(readOnly = true)
    public List<OperacionIGVDtos.Response> listarPorPeriodo(Usuario empresa, LocalDate fechaInicio, LocalDate fechaFin) {
        return repository.findByEmpresaAndFechaOperacionBetweenAndActivoTrue(empresa, fechaInicio, fechaFin)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public OperacionIGVDtos.ResumenIGV calcularResumenMensual(Usuario empresa, int mes, int anio) {
        BigDecimal igvVentas = repository.calcularIGVVentasMensual(empresa, mes, anio);
        BigDecimal igvCompras = repository.calcularIGVComprasMensual(empresa, mes, anio);
        BigDecimal baseVentas = repository.calcularBaseImponibleVentasMensual(empresa, mes, anio);
        BigDecimal baseCompras = repository.calcularBaseImponibleComprasMensual(empresa, mes, anio);
        
        igvVentas = igvVentas != null ? igvVentas : BigDecimal.ZERO;
        igvCompras = igvCompras != null ? igvCompras : BigDecimal.ZERO;
        baseVentas = baseVentas != null ? baseVentas : BigDecimal.ZERO;
        baseCompras = baseCompras != null ? baseCompras : BigDecimal.ZERO;
        
        BigDecimal totalVentas = baseVentas.add(igvVentas);
        BigDecimal totalCompras = baseCompras.add(igvCompras);
        BigDecimal igvAPagar = igvVentas.subtract(igvCompras);
        
        return OperacionIGVDtos.ResumenIGV.builder()
                .mes(mes)
                .anio(anio)
                .totalVentas(totalVentas)
                .igvVentas(igvVentas)
                .totalCompras(totalCompras)
                .igvCompras(igvCompras)
                .igvAPagar(igvAPagar)
                .build();
    }
    
    private OperacionIGVDtos.Response toDto(OperacionIGV operacion) {
        return OperacionIGVDtos.Response.builder()
                .id(operacion.getId())
                .tipo(operacion.getTipo())
                .numeroDocumento(operacion.getNumeroDocumento())
                .fechaOperacion(operacion.getFechaOperacion())
                .razonSocialTercero(operacion.getRazonSocialTercero())
                .rucTercero(operacion.getRucTercero())
                .baseImponible(operacion.getBaseImponible())
                .igv(operacion.getIgv())
                .montoTotal(operacion.getMontoTotal())
                .descripcion(operacion.getDescripcion())
                .activo(operacion.getActivo())
                .fechaCreacion(operacion.getFechaCreacion())
                .fechaActualizacion(operacion.getFechaActualizacion())
                .build();
    }
}
