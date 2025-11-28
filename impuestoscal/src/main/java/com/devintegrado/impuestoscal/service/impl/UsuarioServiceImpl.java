package com.devintegrado.impuestoscal.service.impl;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devintegrado.impuestoscal.dto.UsuarioRegisterRequest;
import com.devintegrado.impuestoscal.model.Rol;
import com.devintegrado.impuestoscal.model.RoleName;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.RolRepository;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import com.devintegrado.impuestoscal.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario crearUsuario(UsuarioRegisterRequest request) {
        if (usuarioRepository.existsByRut10(request.getRut10())) {
            throw new IllegalArgumentException("El rut10 ya está registrado");
        }
        RoleName roleName = "JURIDICA".equalsIgnoreCase(request.getTipoPersona()) ? RoleName.USUARIO_JURIDICO : RoleName.USUARIO_NATURAL;
        Rol rol = rolRepository.findByNombre(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));
        Usuario usuario = Usuario.builder()
                .rut10(request.getRut10())
                .nombre(request.getNombre())
                .tipoPersona(request.getTipoPersona())
                .claveSolHash(passwordEncoder.encode(request.getClaveSol()))
                .roles(Set.of(rol))
                .build();
        return usuarioRepository.save(usuario);
    }
}
