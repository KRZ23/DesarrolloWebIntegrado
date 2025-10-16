package com.devintegrado.impuestoscal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devintegrado.impuestoscal.dto.TrabajadorDtos;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import com.devintegrado.impuestoscal.service.TrabajadorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trabajadores")
@PreAuthorize("hasAnyRole('USUARIO_JURIDICO', 'ADMIN')")
public class TrabajadorController extends BaseController {
    
    private final TrabajadorService service;
    
    public TrabajadorController(TrabajadorService service, UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<List<TrabajadorDtos.Response>> listar(Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.listarPorEmpresa(usuario));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TrabajadorDtos.Response> obtener(@PathVariable Long id, Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.buscarPorId(id, usuario));
    }
    
    @PostMapping
    public ResponseEntity<TrabajadorDtos.Response> crear(
            @Valid @RequestBody TrabajadorDtos.CreateRequest request,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        TrabajadorDtos.Response response = service.crear(request, usuario);
        return ResponseEntity.created(URI.create("/api/trabajadores/" + response.getId()))
                .body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TrabajadorDtos.Response> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TrabajadorDtos.UpdateRequest request,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.actualizar(id, request, usuario));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        service.eliminar(id, usuario);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/resumen-planilla")
    public ResponseEntity<TrabajadorDtos.ResumenPlanilla> calcularResumenPlanilla(
            @RequestParam int mes,
            @RequestParam int anio,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.calcularResumenPlanilla(usuario, mes, anio));
    }
}
