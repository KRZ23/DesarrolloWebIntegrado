package com.devintegrado.impuestoscal.repository;

import com.devintegrado.impuestoscal.model.Rol;
import com.devintegrado.impuestoscal.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RoleName nombre);
}



