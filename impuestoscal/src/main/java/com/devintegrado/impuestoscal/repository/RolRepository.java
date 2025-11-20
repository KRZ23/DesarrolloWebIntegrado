package com.devintegrado.impuestoscal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devintegrado.impuestoscal.model.Rol;
import com.devintegrado.impuestoscal.model.RoleName;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RoleName nombre);
}



