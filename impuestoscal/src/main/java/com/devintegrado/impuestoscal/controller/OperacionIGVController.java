package com.devintegrado.impuestoscal.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.devintegrado.impuestoscal.dto.OperacionIGVDtos;
import com.devintegrado.impuestoscal.model.TipoOperacionIGV;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import com.devintegrado.impuestoscal.service.OperacionIGVService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/operaciones-igv")
@PreAuthorize("hasAnyRole('USUARIO_JURIDICO', 'ADMIN')")
public class OperacionIGVController extends BaseController {
    
    private final OperacionIGVService service;
    
    public OperacionIGVController(OperacionIGVService service, UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<List<OperacionIGVDtos.Response>> listar(Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.listarPorEmpresa(usuario));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OperacionIGVDtos.Response> obtener(@PathVariable Long id, Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.buscarPorId(id, usuario));
    }
    
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<OperacionIGVDtos.Response>> listarPorTipo(
            @PathVariable TipoOperacionIGV tipo,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.listarPorTipo(usuario, tipo));
    }
    
    @PostMapping
    public ResponseEntity<OperacionIGVDtos.Response> crear(
            @Valid @RequestBody OperacionIGVDtos.CreateRequest request,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        OperacionIGVDtos.Response response = service.crear(request, usuario);
        return ResponseEntity.created(URI.create("/api/operaciones-igv/" + response.getId()))
                .body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OperacionIGVDtos.Response> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OperacionIGVDtos.UpdateRequest request,
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
    
    @GetMapping("/periodo")
    public ResponseEntity<List<OperacionIGVDtos.Response>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.listarPorPeriodo(usuario, fechaInicio, fechaFin));
    }
    
    @GetMapping("/resumen-mensual")
    public ResponseEntity<OperacionIGVDtos.ResumenIGV> calcularResumenMensual(
            @RequestParam int mes,
            @RequestParam int anio,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.calcularResumenMensual(usuario, mes, anio));
    }
}
