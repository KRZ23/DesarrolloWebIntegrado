package com.devintegrado.impuestoscal.config;

import com.devintegrado.impuestoscal.model.*;
import com.devintegrado.impuestoscal.repository.RolRepository;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedData(RolRepository rolRepository,
                               UsuarioRepository usuarioRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // Roles
            for (RoleName rn : RoleName.values()) {
                rolRepository.findByNombre(rn).orElseGet(() -> rolRepository.save(Rol.builder().nombre(rn).build()));
            }

            // Usuarios demo
            if (usuarioRepository.count() == 0) {
                Rol admin = rolRepository.findByNombre(RoleName.ADMIN).orElseThrow();
                Rol nat = rolRepository.findByNombre(RoleName.USUARIO_NATURAL).orElseThrow();
                Rol jur = rolRepository.findByNombre(RoleName.USUARIO_JURIDICO).orElseThrow();

                Usuario u1 = Usuario.builder()
                        .rut10("1234567890")
                        .claveSolHash(passwordEncoder.encode("sol1234"))
                        .roles(Set.of(nat))
                        .build();
                Usuario u2 = Usuario.builder()
                        .rut10("5555555555")
                        .claveSolHash(passwordEncoder.encode("sol1234"))
                        .roles(Set.of(jur))
                        .build();
                Usuario a1 = Usuario.builder()
                        .rut10("0000000000")
                        .claveSolHash(passwordEncoder.encode("admin"))
                        .roles(Set.of(admin))
                        .build();
                usuarioRepository.save(u1);
                usuarioRepository.save(u2);
                usuarioRepository.save(a1);
            }
        };
    }
}



