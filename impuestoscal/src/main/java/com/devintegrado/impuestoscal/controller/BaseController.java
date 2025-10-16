package com.devintegrado.impuestoscal.controller;

import org.springframework.security.core.Authentication;

import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;

/**
 * Clase base para controllers que necesitan acceso al usuario autenticado.
 * Simplifica la obtención del usuario actual sin duplicar código.
 */
public abstract class BaseController {
    
    protected final UsuarioRepository usuarioRepository;
    
    protected BaseController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    /**
     * Obtiene el usuario autenticado desde el contexto de seguridad
     * 
     * @param authentication objeto Authentication de Spring Security
     * @return Usuario autenticado
     * @throws IllegalArgumentException si el usuario no existe
     */
    protected Usuario getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        
        return usuarioRepository.findByRut10(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + authentication.getName()));
    }
    
    /**
     * Valida que el usuario autenticado sea propietario del recurso
     * 
     * @param usuario Usuario autenticado
     * @param propietarioId ID del propietario del recurso
     * @throws IllegalArgumentException si no es propietario
     */
    protected void validateOwnership(Usuario usuario, Long propietarioId) {
        if (!usuario.getId().equals(propietarioId)) {
            // Verificar si es ADMIN
            boolean isAdmin = usuario.getRoles().stream()
                    .anyMatch(rol -> rol.getNombre().name().equals("ADMIN"));
            
            if (!isAdmin) {
                throw new IllegalArgumentException("No autorizado para acceder a este recurso");
            }
        }
    }
}
