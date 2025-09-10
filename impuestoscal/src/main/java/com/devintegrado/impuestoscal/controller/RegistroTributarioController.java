package com.devintegrado.impuestoscal.controller;

import com.devintegrado.impuestoscal.dto.RegistroTributarioDtos;
import com.devintegrado.impuestoscal.model.EstadoRegistro;
import com.devintegrado.impuestoscal.model.RegistroTributario;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.RegistroTributarioRepository;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registros")
public class RegistroTributarioController {
    private final RegistroTributarioRepository registroRepository;
    private final UsuarioRepository usuarioRepository;

    public RegistroTributarioController(RegistroTributarioRepository registroRepository, UsuarioRepository usuarioRepository) {
        this.registroRepository = registroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<RegistroTributarioDtos.Response> listar(Authentication auth) {
        Usuario u = usuarioRepository.findByRut10(auth.getName()).orElseThrow();
        return registroRepository.findByTitular(u).stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<RegistroTributarioDtos.Response> crear(Authentication auth,
                                                                 @Valid @RequestBody RegistroTributarioDtos.CreateOrUpdateRequest body) {
        Usuario u = usuarioRepository.findByRut10(auth.getName()).orElseThrow();
        RegistroTributario reg = RegistroTributario.builder()
                .tipoImpuesto(body.getTipoImpuesto())
                .monto(body.getMonto())
                .fechaVencimiento(body.getFechaVencimiento())
                .estado(body.getEstado() != null ? body.getEstado() : EstadoRegistro.PENDIENTE)
                .titular(u)
                .build();
        reg = registroRepository.save(reg);
        return ResponseEntity.created(URI.create("/api/registros/" + reg.getId())).body(toDto(reg));
    }

    @GetMapping("/pendientes")
    public List<RegistroTributarioDtos.Response> pendientes(Authentication auth) {
        Usuario u = usuarioRepository.findByRut10(auth.getName()).orElseThrow();
        return registroRepository.findByTitularAndEstado(u, EstadoRegistro.PENDIENTE)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/vencidos")
    public List<RegistroTributarioDtos.Response> vencidos(Authentication auth) {
        Usuario u = usuarioRepository.findByRut10(auth.getName()).orElseThrow();
        return registroRepository.findByTitularAndFechaVencimientoBeforeAndEstado(u, LocalDate.now(), EstadoRegistro.PENDIENTE)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public RegistroTributarioDtos.Response actualizar(Authentication auth, @PathVariable Long id,
                                                      @Valid @RequestBody RegistroTributarioDtos.CreateOrUpdateRequest body) {
        Usuario u = usuarioRepository.findByRut10(auth.getName()).orElseThrow();
        RegistroTributario reg = registroRepository.findById(id).orElseThrow();
        if (!reg.getTitular().getId().equals(u.getId())) {
            throw new IllegalArgumentException("No autorizado para modificar este registro");
        }
        reg.setTipoImpuesto(body.getTipoImpuesto());
        reg.setMonto(body.getMonto());
        reg.setFechaVencimiento(body.getFechaVencimiento());
        if (body.getEstado() != null) reg.setEstado(body.getEstado());
        return toDto(registroRepository.save(reg));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication auth, @PathVariable Long id) {
        Usuario u = usuarioRepository.findByRut10(auth.getName()).orElseThrow();
        RegistroTributario reg = registroRepository.findById(id).orElseThrow();
        if (!reg.getTitular().getId().equals(u.getId())) {
            return ResponseEntity.status(403).build();
        }
        registroRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/todos")
    public List<RegistroTributarioDtos.Response> listarTodosAdmin() {
        return registroRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
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



