package com.devintegrado.impuestoscal.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
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

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/registros")
public class RegistroTributarioController extends BaseController {
    private final RegistroTributarioRepository registroRepository;

    public RegistroTributarioController(RegistroTributarioRepository registroRepository, UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
        this.registroRepository = registroRepository;
    }

    @GetMapping
    public List<RegistroTributarioDtos.Response> listar(Authentication auth) {
        Usuario u = getCurrentUser(auth);
        return registroRepository.findByTitular(u).stream()
                .filter(r -> r.getActivo())
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @PostMapping
    public ResponseEntity<RegistroTributarioDtos.Response> crear(Authentication auth,
                                                                 @Valid @RequestBody RegistroTributarioDtos.CreateOrUpdateRequest body) {
        Usuario u = getCurrentUser(auth);
        RegistroTributario reg = RegistroTributario.builder()
                .tipoImpuesto(body.getTipoImpuesto())
                .monto(body.getMonto())
                .fechaVencimiento(body.getFechaVencimiento())
                .estado(body.getEstado() != null ? body.getEstado() : EstadoRegistro.PENDIENTE)
                .activo(true)
                .titular(u)
                .build();
        reg = registroRepository.save(reg);
        return ResponseEntity.created(URI.create("/api/registros/" + reg.getId())).body(toDto(reg));
    }
    
    @GetMapping("/pendientes")
    public List<RegistroTributarioDtos.Response> pendientes(Authentication auth) {
        Usuario u = getCurrentUser(auth);
        return registroRepository.findByTitularAndEstado(u, EstadoRegistro.PENDIENTE)
                .stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @GetMapping("/vencidos")
    public List<RegistroTributarioDtos.Response> vencidos(Authentication auth) {
        Usuario u = getCurrentUser(auth);
        return registroRepository.findByTitularAndFechaVencimientoBeforeAndEstado(u, LocalDate.now(), EstadoRegistro.PENDIENTE)
                .stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @PutMapping("/{id}")
    public RegistroTributarioDtos.Response actualizar(Authentication auth, @PathVariable Long id,
                                                      @Valid @RequestBody RegistroTributarioDtos.CreateOrUpdateRequest body) {
        Usuario u = getCurrentUser(auth);
        RegistroTributario reg = registroRepository.findById(id).orElseThrow();
        validateOwnership(u, reg.getTitular().getId());
        
        if (!reg.getActivo()) {
            throw new IllegalArgumentException("No se puede modificar un registro inactivo");
        }
        reg.setTipoImpuesto(body.getTipoImpuesto());
        reg.setMonto(body.getMonto());
        reg.setFechaVencimiento(body.getFechaVencimiento());
        if (body.getEstado() != null) reg.setEstado(body.getEstado());
        return toDto(registroRepository.save(reg));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication auth, @PathVariable Long id) {
        Usuario u = getCurrentUser(auth);
        RegistroTributario reg = registroRepository.findById(id).orElseThrow();
        validateOwnership(u, reg.getTitular().getId());
        
        // Eliminación lógica: cambiar estado a inactivo
        reg.setActivo(false);
        registroRepository.save(reg);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/proximos-vencimientos")
    public List<RegistroTributarioDtos.Response> proximosVencimientos(
            Authentication auth,
            @RequestParam(defaultValue = "7") int dias) {
        Usuario u = getCurrentUser(auth);
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return registroRepository.findByTitularAndFechaVencimientoBeforeAndEstado(u, fechaLimite, EstadoRegistro.PENDIENTE)
                .stream()
                .filter(r -> r.getFechaVencimiento().isAfter(LocalDate.now()) || r.getFechaVencimiento().isEqual(LocalDate.now()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }    @PreAuthorize("hasRole('ADMIN')")
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



