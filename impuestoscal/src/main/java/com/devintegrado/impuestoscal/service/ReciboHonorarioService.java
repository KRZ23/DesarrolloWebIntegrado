package com.devintegrado.impuestoscal.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.devintegrado.impuestoscal.dto.ReciboHonorarioDtos;
import com.devintegrado.impuestoscal.model.Usuario;

public interface ReciboHonorarioService {
    
    List<ReciboHonorarioDtos.Response> listarPorEmisor(Usuario emisor);
    
    ReciboHonorarioDtos.Response buscarPorId(Long id, Usuario emisor);
    
    ReciboHonorarioDtos.Response crear(ReciboHonorarioDtos.CreateRequest request, Usuario emisor);
    
    ReciboHonorarioDtos.Response actualizar(Long id, ReciboHonorarioDtos.UpdateRequest request, Usuario emisor);
    
    void eliminar(Long id, Usuario emisor);
    
    List<ReciboHonorarioDtos.Response> listarPorPeriodo(Usuario emisor, LocalDate fechaInicio, LocalDate fechaFin);
    
    BigDecimal calcularRetencionAnual(Usuario emisor, int anio);
    
    BigDecimal calcularIngresosAnuales(Usuario emisor, int anio);
}
