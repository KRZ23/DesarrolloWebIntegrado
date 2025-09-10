package com.devintegrado.impuestoscal.service;

import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String rut10) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByRut10(rut10)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        Set<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre().name()))
                .collect(Collectors.toSet());
        return new User(usuario.getRut10(), usuario.getClaveSolHash(), authorities);
    }
}



