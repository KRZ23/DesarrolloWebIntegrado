package com.devintegrado.impuestoscal.controller;

import com.devintegrado.impuestoscal.dto.RegistroTributarioDtos;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import com.devintegrado.impuestoscal.service.RegistroTributarioService;
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
public class RegistroTributarioController extends BaseController {
    
    private final RegistroTributarioService service;

    public RegistroTributarioController(RegistroTributarioService service, UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
        this.service = service;
    }

    @GetMapping
    public List<RegistroTributarioDtos.Response> listar(Authentication auth) {
        Usuario u = getCurrentUser(auth);
        return service.listar(u);
    }
    
    @PostMapping
    public ResponseEntity<RegistroTributarioDtos.Response> crear(Authentication auth,
                                                                 @Valid @RequestBody RegistroTributarioDtos.CreateOrUpdateRequest body) {
        Usuario u = getCurrentUser(auth);
        RegistroTributarioDtos.Response response = service.crear(body, u);
        return ResponseEntity.created(URI.create("/api/registros/" + response.getId())).body(response);
    }
    
    @GetMapping("/pendientes")
    public List<RegistroTributarioDtos.Response> pendientes(Authentication auth) {
        Usuario u = getCurrentUser(auth);
        return service.listarPendientes(u);
    }
    
    @GetMapping("/vencidos")
    public List<RegistroTributarioDtos.Response> vencidos(Authentication auth) {
        Usuario u = getCurrentUser(auth);
        return service.listarVencidos(u);
    }
    
    @PutMapping("/{id}")
    public RegistroTributarioDtos.Response actualizar(Authentication auth, @PathVariable Long id,
                                                      @Valid @RequestBody RegistroTributarioDtos.CreateOrUpdateRequest body) {
        Usuario u = getCurrentUser(auth);
        return service.actualizar(id, body, u);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication auth, @PathVariable Long id) {
        Usuario u = getCurrentUser(auth);
        service.eliminar(id, u);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/proximos-vencimientos")
    public List<RegistroTributarioDtos.Response> proximosVencimientos(
            Authentication auth,
            @RequestParam(defaultValue = "7") int dias) {
        Usuario u = getCurrentUser(auth);
        return service.listarProximosVencimientos(u, dias);
    }

}



