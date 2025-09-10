package com.devintegrado.impuestoscal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devintegrado.impuestoscal.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByRut10(String rut10);
    boolean existsByRut10(String rut10);
}



