package com.devintegrado.impuestoscal.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devintegrado.impuestoscal.model.OperacionIGV;
import com.devintegrado.impuestoscal.model.Rol;
import com.devintegrado.impuestoscal.model.RoleName;
import com.devintegrado.impuestoscal.model.TipoOperacionIGV;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.RolRepository;
import com.devintegrado.impuestoscal.repository.UsuarioRepository;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedData(RolRepository rolRepository,
                               UsuarioRepository usuarioRepository,
                               com.devintegrado.impuestoscal.repository.OperacionIGVRepository operacionRepository,
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
                        .nombre("Juan Pérez")
                        .tipoPersona("NATURAL")
                        .claveSolHash(passwordEncoder.encode("sol1234"))
                        .roles(Set.of(nat))
                        .build();
                Usuario u2 = Usuario.builder()
                        .rut10("5555555555")
                        .nombre("Empresa ABC S.A.C.")
                        .tipoPersona("JURIDICA")
                        .claveSolHash(passwordEncoder.encode("sol1234"))
                        .roles(Set.of(jur))
                        .build();
                Usuario a1 = Usuario.builder()
                        .rut10("0000000000")
                        .nombre("Administrador")
                        .tipoPersona("NATURAL")
                        .claveSolHash(passwordEncoder.encode("admin"))
                        .roles(Set.of(admin))
                        .build();
                usuarioRepository.save(u1);
                usuarioRepository.save(u2);
                usuarioRepository.save(a1);

                // Datos de prueba para OperacionIGV (Noviembre 2025)
                java.time.LocalDate fecha = java.time.LocalDate.of(2025, 11, 15);
                
                // Venta 1
                operacionRepository.save(OperacionIGV.builder()
                    .tipo(TipoOperacionIGV.VENTA)
                    .numeroDocumento("F001-123")
                    .fechaOperacion(fecha)
                    .razonSocialTercero("Cliente X")
                    .rucTercero("20123456789")
                    .baseImponible(new java.math.BigDecimal("1000.00"))
                    .descripcion("Venta de servicios")
                    .empresa(u2)
                    .build());

                // Venta 2
                operacionRepository.save(OperacionIGV.builder()
                    .tipo(TipoOperacionIGV.VENTA)
                    .numeroDocumento("F001-124")
                    .fechaOperacion(fecha.plusDays(1))
                    .razonSocialTercero("Cliente Y")
                    .rucTercero("20987654321")
                    .baseImponible(new java.math.BigDecimal("2500.00"))
                    .descripcion("Venta de productos")
                    .empresa(u2)
                    .build());

                // Compra 1
                operacionRepository.save(OperacionIGV.builder()
                    .tipo(TipoOperacionIGV.COMPRA)
                    .numeroDocumento("F002-999")
                    .fechaOperacion(fecha.minusDays(2))
                    .razonSocialTercero("Proveedor Z")
                    .rucTercero("20555666777")
                    .baseImponible(new java.math.BigDecimal("800.00"))
                    .descripcion("Compra de insumos")
                    .empresa(u2)
                    .build());
            }
        };
    }
}



