package com.devintegrado.impuestoscal.service;

import java.time.LocalDate;
import java.util.List;

import com.devintegrado.impuestoscal.dto.OperacionIGVDtos;
import com.devintegrado.impuestoscal.model.TipoOperacionIGV;
import com.devintegrado.impuestoscal.model.Usuario;

public interface OperacionIGVService {
    
    List<OperacionIGVDtos.Response> listarPorEmpresa(Usuario empresa);
    
    List<OperacionIGVDtos.Response> listarPorTipo(Usuario empresa, TipoOperacionIGV tipo);
    
    OperacionIGVDtos.Response buscarPorId(Long id, Usuario empresa);
    
    OperacionIGVDtos.Response crear(OperacionIGVDtos.CreateRequest request, Usuario empresa);
    
    OperacionIGVDtos.Response actualizar(Long id, OperacionIGVDtos.UpdateRequest request, Usuario empresa);
    
    void eliminar(Long id, Usuario empresa);
    
    List<OperacionIGVDtos.Response> listarPorPeriodo(Usuario empresa, LocalDate fechaInicio, LocalDate fechaFin);
    
    OperacionIGVDtos.ResumenIGV calcularResumenMensual(Usuario empresa, int mes, int anio);
}
