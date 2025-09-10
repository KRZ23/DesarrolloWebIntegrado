package com.devintegrado.impuestoscal.repository;

import com.devintegrado.impuestoscal.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByRut10(String rut10);
    boolean existsByRut10(String rut10);
}



