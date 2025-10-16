package com.devintegrado.impuestoscal.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.devintegrado.impuestoscal.model.RoleName;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;

/**
 * Utilidad para simplificar la obtención del usuario autenticado
 */
@Component
public class AuthenticationHelper {
    
    private final UsuarioRepository usuarioRepository;
    
    public AuthenticationHelper(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    /**
     * Obtiene el usuario autenticado actual desde el Authentication
     * 
     * @param authentication objeto Authentication de Spring Security
     * @return Usuario autenticado
     * @throws IllegalArgumentException si el usuario no existe
     */
    public Usuario getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        
        return usuarioRepository.findByRut10(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + authentication.getName()));
    }
    
    /**
     * Verifica si el usuario autenticado es propietario del recurso
     * 
     * @param authentication objeto Authentication
     * @param propietarioId ID del propietario del recurso
     * @return true si es propietario o es admin
     */
    public boolean isOwnerOrAdmin(Authentication authentication, Long propietarioId) {
        Usuario usuario = getCurrentUser(authentication);
        return usuario.getId().equals(propietarioId) || 
               usuario.getRoles().stream().anyMatch(rol -> rol.getNombre() == RoleName.ADMIN);
    }
    
    /**
     * Valida que el usuario tenga permiso sobre un recurso
     * 
     * @param authentication objeto Authentication
     * @param propietarioId ID del propietario del recurso
     * @throws IllegalArgumentException si no tiene permiso
     */
    public void validateOwnership(Authentication authentication, Long propietarioId) {
        Usuario usuario = getCurrentUser(authentication);
        if (!usuario.getId().equals(propietarioId)) {
            boolean isAdmin = usuario.getRoles().stream()
                    .anyMatch(rol -> rol.getNombre() == RoleName.ADMIN);
            
            if (!isAdmin) {
                throw new IllegalArgumentException("No autorizado para acceder a este recurso");
            }
        }
    }
    
    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(Authentication authentication, RoleName roleName) {
        Usuario usuario = getCurrentUser(authentication);
        return usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == roleName);
    }
}
