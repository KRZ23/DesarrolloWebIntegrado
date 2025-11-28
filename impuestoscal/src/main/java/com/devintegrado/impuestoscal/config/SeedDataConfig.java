package com.devintegrado.impuestoscal.config;

import com.devintegrado.impuestoscal.model.*;
import com.devintegrado.impuestoscal.repository.RegistroTributarioRepository;
import com.devintegrado.impuestoscal.repository.RolRepository;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedData(RolRepository rolRepository,
                               UsuarioRepository usuarioRepository,
                               RegistroTributarioRepository registroRepository,
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

                // ========================================
                // USUARIOS DE PRUEBA - CREDENCIALES
                // ========================================
                
                // Usuario Natural
                // RUT: 1020304050
                // Password: natural123
                Usuario u1 = Usuario.builder()
                        .rut10("1020304050")
                        .nombre("Juan Pérez")
                        .tipoPersona("NATURAL")
                        .claveSolHash(passwordEncoder.encode("natural123"))
                        .roles(Set.of(nat))
                        .build();
                
                // Empresa (Usuario Jurídico)
                // RUT: 2010020030
                // Password: empresa123
                Usuario u2 = Usuario.builder()
                        .rut10("2010020030")
                        .nombre("Empresa ABC S.A.C.")
                        .tipoPersona("JURIDICA")
                        .claveSolHash(passwordEncoder.encode("empresa123"))
                        .roles(Set.of(jur))
                        .build();
                
                // Administrador
                // RUT: 2030040050
                // Password: admin123
                Usuario a1 = Usuario.builder()
                        .rut10("2030040050")
                        .nombre("Administrador")
                        .tipoPersona("NATURAL")
                        .claveSolHash(passwordEncoder.encode("admin123"))
                        .roles(Set.of(admin))
                        .build();
                        
                usuarioRepository.save(u1);
                usuarioRepository.save(u2);
                usuarioRepository.save(a1);

                // Crear registros tributarios de ejemplo
                // Para Usuario Natural (u1)
                RegistroTributario rt1 = RegistroTributario.builder()
                        .tipoImpuesto("Renta 4ta Categoría")
                        .monto(new BigDecimal("350.00"))
                        .fechaVencimiento(LocalDate.now().plusDays(10))
                        .estado(EstadoRegistro.PENDIENTE)
                        .titular(u1)
                        .observaciones("Pago mensual de renta")
                        .build();
                registroRepository.save(rt1);

                RegistroTributario rt2 = RegistroTributario.builder()
                        .tipoImpuesto("Renta Anual")
                        .monto(new BigDecimal("1200.00"))
                        .fechaVencimiento(LocalDate.now().minusDays(5))
                        .estado(EstadoRegistro.PENDIENTE)
                        .titular(u1)
                        .observaciones("Declaración anual vencida")
                        .build();
                registroRepository.save(rt2);

                // Para Empresa (u2)
                RegistroTributario rt3 = RegistroTributario.builder()
                        .tipoImpuesto("IGV Mensual")
                        .monto(new BigDecimal("4500.00"))
                        .fechaVencimiento(LocalDate.now().plusDays(15))
                        .estado(EstadoRegistro.PENDIENTE)
                        .titular(u2)
                        .observaciones("IGV del mes actual")
                        .build();
                registroRepository.save(rt3);

                RegistroTributario rt4 = RegistroTributario.builder()
                        .tipoImpuesto("Renta 3ra Categoría")
                        .monto(new BigDecimal("8900.00"))
                        .fechaVencimiento(LocalDate.now().plusMonths(1))
                        .estado(EstadoRegistro.PENDIENTE)
                        .titular(u2)
                        .observaciones("Pago a cuenta mensual")
                        .build();
                registroRepository.save(rt4);

                RegistroTributario rt5 = RegistroTributario.builder()
                        .tipoImpuesto("IGV Mensual")
                        .monto(new BigDecimal("4200.00"))
                        .fechaVencimiento(LocalDate.now().minusDays(10))
                        .estado(EstadoRegistro.PAGADO)
                        .titular(u2)
                        .observaciones("IGV del mes anterior - Pagado")
                        .build();
                registroRepository.save(rt5);

                RegistroTributario rt6 = RegistroTributario.builder()
                        .tipoImpuesto("Essalud")
                        .monto(new BigDecimal("1500.00"))
                        .fechaVencimiento(LocalDate.now().plusDays(20))
                        .estado(EstadoRegistro.PENDIENTE)
                        .titular(u2)
                        .observaciones("Contribuciones sociales")
                        .build();
                registroRepository.save(rt6);
            }
        };
    }
}
