package com.devintegrado.impuestoscal.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devintegrado.impuestoscal.model.EstadoRegistro;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.RegistroTributarioRepository;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final RegistroTributarioRepository registroRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardController(RegistroTributarioRepository registroRepository, UsuarioRepository usuarioRepository) {
        this.registroRepository = registroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> resumen(Authentication auth) {
        Usuario u = usuarioRepository.findByRut10(auth.getName()).orElseThrow();
        int total = registroRepository.findByTitular(u).size();
        int pendientes = registroRepository.findByTitularAndEstado(u, EstadoRegistro.PENDIENTE).size();
        int vencidos = registroRepository
                .findByTitularAndFechaVencimientoBeforeAndEstado(u, LocalDate.now(), EstadoRegistro.PENDIENTE)
                .size();
        Map<String, Object> payload = new HashMap<>();
        payload.put("total", total);
        payload.put("pendientes", pendientes);
        payload.put("vencidos", vencidos);
        return ResponseEntity.ok(payload);
    }
}


