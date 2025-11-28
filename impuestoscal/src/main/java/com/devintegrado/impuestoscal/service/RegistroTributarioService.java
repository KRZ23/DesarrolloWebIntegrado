package com.devintegrado.impuestoscal.service;

import com.devintegrado.impuestoscal.dto.RegistroTributarioDtos;
import com.devintegrado.impuestoscal.model.Usuario;
import java.time.LocalDate;
import java.util.List;

public interface RegistroTributarioService {
    List<RegistroTributarioDtos.Response> listar(Usuario usuario);
    RegistroTributarioDtos.Response crear(RegistroTributarioDtos.CreateOrUpdateRequest request, Usuario usuario);
    RegistroTributarioDtos.Response actualizar(Long id, RegistroTributarioDtos.CreateOrUpdateRequest request, Usuario usuario);
    void eliminar(Long id, Usuario usuario);
    List<RegistroTributarioDtos.Response> listarPendientes(Usuario usuario);
    List<RegistroTributarioDtos.Response> listarVencidos(Usuario usuario);
    List<RegistroTributarioDtos.Response> listarProximosVencimientos(Usuario usuario, int dias);
}
