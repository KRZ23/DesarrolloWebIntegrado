package com.devintegrado.impuestoscal.controller;

import java.math.BigDecimal;
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

import com.devintegrado.impuestoscal.dto.ReciboHonorarioDtos;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import com.devintegrado.impuestoscal.service.ReciboHonorarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recibos-honorarios")
@PreAuthorize("hasAnyRole('USUARIO_NATURAL', 'ADMIN')")
public class ReciboHonorarioController extends BaseController {
    
    private final ReciboHonorarioService service;
    
    public ReciboHonorarioController(ReciboHonorarioService service, UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<List<ReciboHonorarioDtos.Response>> listar(Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.listarPorEmisor(usuario));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReciboHonorarioDtos.Response> obtener(@PathVariable Long id, Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.buscarPorId(id, usuario));
    }
    
    @PostMapping
    public ResponseEntity<ReciboHonorarioDtos.Response> crear(
            @Valid @RequestBody ReciboHonorarioDtos.CreateRequest request,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        ReciboHonorarioDtos.Response response = service.crear(request, usuario);
        return ResponseEntity.created(URI.create("/api/recibos-honorarios/" + response.getId()))
                .body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ReciboHonorarioDtos.Response> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReciboHonorarioDtos.UpdateRequest request,
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
    public ResponseEntity<List<ReciboHonorarioDtos.Response>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.listarPorPeriodo(usuario, fechaInicio, fechaFin));
    }
    
    @GetMapping("/retencion-anual")
    public ResponseEntity<BigDecimal> calcularRetencionAnual(
            @RequestParam int anio,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.calcularRetencionAnual(usuario, anio));
    }
    
    @GetMapping("/ingresos-anuales")
    public ResponseEntity<BigDecimal> calcularIngresosAnuales(
            @RequestParam int anio,
            Authentication auth) {
        Usuario usuario = getCurrentUser(auth);
        return ResponseEntity.ok(service.calcularIngresosAnuales(usuario, anio));
    }
}
