package com.devintegrado.impuestoscal.controller;

import com.devintegrado.impuestoscal.dto.UsuarioRegisterRequest;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody UsuarioRegisterRequest request) {
        Usuario usuario = usuarioService.crearUsuario(request);
        return ResponseEntity.ok(usuario);
    }
}
