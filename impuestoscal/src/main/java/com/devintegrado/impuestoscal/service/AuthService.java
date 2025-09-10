package com.devintegrado.impuestoscal.service;

import com.devintegrado.impuestoscal.dto.AuthDtos;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import com.devintegrado.impuestoscal.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getRut10(), request.getClaveSol())
        );
        Usuario usuario = usuarioRepository.findByRut10(request.getRut10()).orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", usuario.getRoles().stream().map(r -> r.getNombre().name()).collect(Collectors.toSet()));
        String token = jwtUtil.generateToken(usuario.getRut10(), claims);
        return AuthDtos.LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .rut10(usuario.getRut10())
                .roles(usuario.getRoles().stream().map(r -> r.getNombre().name()).collect(Collectors.toSet()))
                .build();
    }
}



