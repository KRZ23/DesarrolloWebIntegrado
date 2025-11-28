package com.devintegrado.impuestoscal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devintegrado.impuestoscal.dto.ReciboHonorarioDtos;
import com.devintegrado.impuestoscal.model.ReciboHonorario;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.ReciboHonorarioRepository;
import com.devintegrado.impuestoscal.service.ReciboHonorarioService;

@Service
public class ReciboHonorarioServiceImpl implements ReciboHonorarioService {
    
    private final ReciboHonorarioRepository repository;
    
    public ReciboHonorarioServiceImpl(ReciboHonorarioRepository repository) {
        this.repository = repository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReciboHonorarioDtos.Response> listarPorEmisor(Usuario emisor) {
        return repository.findByEmisorAndActivoTrue(emisor).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReciboHonorarioDtos.Response buscarPorId(Long id, Usuario emisor) {
        ReciboHonorario recibo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recibo no encontrado"));
        
        if (!recibo.getEmisor().getId().equals(emisor.getId())) {
            throw new IllegalArgumentException("No autorizado para ver este recibo");
        }
        
        return toDto(recibo);
    }
    
    @Override
    @Transactional
    public ReciboHonorarioDtos.Response crear(ReciboHonorarioDtos.CreateRequest request, Usuario emisor) {
        // Validar que no exista otro recibo con el mismo número
        repository.findByNumeroReciboAndActivoTrue(request.getNumeroRecibo())
                .ifPresent(r -> {
                    throw new IllegalArgumentException("Ya existe un recibo con ese número");
                });
        
        // Calcular retención del 8%
        BigDecimal retencion = request.getMontoTotal().multiply(new BigDecimal("0.08"));
        BigDecimal montoNeto = request.getMontoTotal().subtract(retencion);
        
        ReciboHonorario recibo = ReciboHonorario.builder()
                .numeroRecibo(request.getNumeroRecibo())
                .fechaEmision(request.getFechaEmision())
                .montoTotal(request.getMontoTotal())
                .retencion(retencion)
                .montoNeto(montoNeto)
                .descripcionServicio(request.getDescripcionServicio())
                .clienteRazonSocial(request.getClienteRazonSocial())
                .clienteRuc(request.getClienteRuc())
                .emisor(emisor)
                .activo(true)
                .build();
        
        recibo = repository.save(recibo);
        return toDto(recibo);
    }
    
    @Override
    @Transactional
    public ReciboHonorarioDtos.Response actualizar(Long id, ReciboHonorarioDtos.UpdateRequest request, Usuario emisor) {
        ReciboHonorario recibo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recibo no encontrado"));
        
        if (!recibo.getEmisor().getId().equals(emisor.getId())) {
            throw new IllegalArgumentException("No autorizado para modificar este recibo");
        }
        
        if (!recibo.getActivo()) {
            throw new IllegalArgumentException("No se puede modificar un recibo inactivo");
        }
        
        // Actualizar campos
        if (request.getFechaEmision() != null) {
            recibo.setFechaEmision(request.getFechaEmision());
        }
        if (request.getMontoTotal() != null) {
            recibo.setMontoTotal(request.getMontoTotal());
            // Recalcular retención
            BigDecimal retencion = request.getMontoTotal().multiply(new BigDecimal("0.08"));
            recibo.setRetencion(retencion);
            recibo.setMontoNeto(request.getMontoTotal().subtract(retencion));
        }
        if (request.getDescripcionServicio() != null) {
            recibo.setDescripcionServicio(request.getDescripcionServicio());
        }
        if (request.getClienteRazonSocial() != null) {
            recibo.setClienteRazonSocial(request.getClienteRazonSocial());
        }
        if (request.getClienteRuc() != null) {
            recibo.setClienteRuc(request.getClienteRuc());
        }
        
        recibo = repository.save(recibo);
        return toDto(recibo);
    }
    
    @Override
    @Transactional
    public void eliminar(Long id, Usuario emisor) {
        ReciboHonorario recibo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recibo no encontrado"));
        
        if (!recibo.getEmisor().getId().equals(emisor.getId())) {
            throw new IllegalArgumentException("No autorizado para eliminar este recibo");
        }
        
        // Eliminación lógica: cambiar estado a inactivo
        recibo.setActivo(false);
        repository.save(recibo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReciboHonorarioDtos.Response> listarPorPeriodo(Usuario emisor, LocalDate fechaInicio, LocalDate fechaFin) {
        return repository.findByEmisorAndFechaEmisionBetweenAndActivoTrue(emisor, fechaInicio, fechaFin)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularRetencionAnual(Usuario emisor, int anio) {
        BigDecimal total = repository.calcularRetencionAnual(emisor, anio);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularIngresosAnuales(Usuario emisor, int anio) {
        BigDecimal total = repository.calcularIngresosAnuales(emisor, anio);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private ReciboHonorarioDtos.Response toDto(ReciboHonorario recibo) {
        return ReciboHonorarioDtos.Response.builder()
                .id(recibo.getId())
                .numeroRecibo(recibo.getNumeroRecibo())
                .fechaEmision(recibo.getFechaEmision())
                .montoTotal(recibo.getMontoTotal())
                .retencion(recibo.getRetencion())
                .montoNeto(recibo.getMontoNeto())
                .descripcionServicio(recibo.getDescripcionServicio())
                .clienteRazonSocial(recibo.getClienteRazonSocial())
                .clienteRuc(recibo.getClienteRuc())
                .activo(recibo.getActivo())
                .fechaCreacion(recibo.getFechaCreacion())
                .fechaActualizacion(recibo.getFechaActualizacion())
                .build();
    }
}
