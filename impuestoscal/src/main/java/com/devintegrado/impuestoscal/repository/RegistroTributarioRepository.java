package com.devintegrado.impuestoscal.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devintegrado.impuestoscal.model.EstadoRegistro;
import com.devintegrado.impuestoscal.model.RegistroTributario;
import com.devintegrado.impuestoscal.model.Usuario;

public interface RegistroTributarioRepository extends JpaRepository<RegistroTributario, Long> {
    List<RegistroTributario> findByTitular(Usuario titular);
    List<RegistroTributario> findByTitularAndEstado(Usuario titular, EstadoRegistro estado);
    List<RegistroTributario> findByTitularAndFechaVencimientoBeforeAndEstado(Usuario titular, LocalDate fecha, EstadoRegistro estado);
}



