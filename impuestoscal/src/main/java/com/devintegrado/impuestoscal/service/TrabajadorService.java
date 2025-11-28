package com.devintegrado.impuestoscal.service;

import java.util.List;

import com.devintegrado.impuestoscal.dto.TrabajadorDtos;
import com.devintegrado.impuestoscal.model.Usuario;

public interface TrabajadorService {
    
    List<TrabajadorDtos.Response> listarPorEmpresa(Usuario empresa);
    
    TrabajadorDtos.Response buscarPorId(Long id, Usuario empresa);
    
    TrabajadorDtos.Response crear(TrabajadorDtos.CreateRequest request, Usuario empresa);
    
    TrabajadorDtos.Response actualizar(Long id, TrabajadorDtos.UpdateRequest request, Usuario empresa);
    
    void eliminar(Long id, Usuario empresa);
    
    TrabajadorDtos.ResumenPlanilla calcularResumenPlanilla(Usuario empresa, int mes, int anio);
}
