package com.devintegrado.impuestoscal.service.impl;

import com.devintegrado.impuestoscal.dto.RegistroTributarioDtos;
import com.devintegrado.impuestoscal.model.EstadoRegistro;
import com.devintegrado.impuestoscal.model.RegistroTributario;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.RegistroTributarioRepository;
import com.devintegrado.impuestoscal.service.RegistroTributarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegistroTributarioServiceImpl implements RegistroTributarioService {

    private final RegistroTributarioRepository registroRepository;

    public RegistroTributarioServiceImpl(RegistroTributarioRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @Override
    public List<RegistroTributarioDtos.Response> listar(Usuario usuario) {
        return registroRepository.findByTitular(usuario).stream()
                .filter(RegistroTributario::getActivo)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RegistroTributarioDtos.Response crear(RegistroTributarioDtos.CreateOrUpdateRequest request, Usuario usuario) {
        RegistroTributario reg = RegistroTributario.builder()
                .tipoImpuesto(request.getTipoImpuesto())
                .monto(request.getMonto())
                .fechaVencimiento(request.getFechaVencimiento())
                .estado(request.getEstado() != null ? request.getEstado() : EstadoRegistro.PENDIENTE)
                .activo(true)
                .titular(usuario)
                .build();
        return toDto(registroRepository.save(reg));
    }

    @Override
    public RegistroTributarioDtos.Response actualizar(Long id, RegistroTributarioDtos.CreateOrUpdateRequest request, Usuario usuario) {
        RegistroTributario reg = registroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        
        if (!reg.getTitular().getId().equals(usuario.getId())) {
            throw new RuntimeException("No autorizado");
        }
        
        if (!reg.getActivo()) {
            throw new IllegalArgumentException("No se puede modificar un registro inactivo");
        }

        reg.setTipoImpuesto(request.getTipoImpuesto());
        reg.setMonto(request.getMonto());
        reg.setFechaVencimiento(request.getFechaVencimiento());
        if (request.getEstado() != null) reg.setEstado(request.getEstado());

        return toDto(registroRepository.save(reg));
    }

    @Override
    public void eliminar(Long id, Usuario usuario) {
        RegistroTributario reg = registroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        
        if (!reg.getTitular().getId().equals(usuario.getId())) {
            throw new RuntimeException("No autorizado");
        }

        reg.setActivo(false);
        registroRepository.save(reg);
    }

    @Override
    public List<RegistroTributarioDtos.Response> listarPendientes(Usuario usuario) {
        return registroRepository.findByTitularAndEstado(usuario, EstadoRegistro.PENDIENTE).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegistroTributarioDtos.Response> listarVencidos(Usuario usuario) {
        return registroRepository.findByTitularAndFechaVencimientoBeforeAndEstado(usuario, LocalDate.now(), EstadoRegistro.PENDIENTE).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegistroTributarioDtos.Response> listarProximosVencimientos(Usuario usuario, int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return registroRepository.findByTitularAndFechaVencimientoBeforeAndEstado(usuario, fechaLimite, EstadoRegistro.PENDIENTE).stream()
                .filter(r -> !r.getFechaVencimiento().isBefore(LocalDate.now()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RegistroTributarioDtos.Response toDto(RegistroTributario r) {
        return RegistroTributarioDtos.Response.builder()
                .id(r.getId())
                .tipoImpuesto(r.getTipoImpuesto())
                .monto(r.getMonto())
                .fechaVencimiento(r.getFechaVencimiento())
                .estado(r.getEstado())
                .build();
    }
}
