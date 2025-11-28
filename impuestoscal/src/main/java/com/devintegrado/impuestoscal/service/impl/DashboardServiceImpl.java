package com.devintegrado.impuestoscal.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.devintegrado.impuestoscal.dto.OperacionIGVDtos;
import com.devintegrado.impuestoscal.model.EstadoRegistro;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.RegistroTributarioRepository;
import com.devintegrado.impuestoscal.service.DashboardService;
import com.devintegrado.impuestoscal.service.OperacionIGVService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final RegistroTributarioRepository registroRepository;
    private final com.devintegrado.impuestoscal.repository.UsuarioRepository usuarioRepository;
    private final OperacionIGVService operacionIGVService;

    public DashboardServiceImpl(RegistroTributarioRepository registroRepository, 
                                com.devintegrado.impuestoscal.repository.UsuarioRepository usuarioRepository,
                                OperacionIGVService operacionIGVService) {
        this.registroRepository = registroRepository;
        this.usuarioRepository = usuarioRepository;
        this.operacionIGVService = operacionIGVService;
    }

    @Override
    public Map<String, Object> obtenerResumen(String rut10) {
        Usuario usuario = usuarioRepository.findByRut10(rut10).orElseThrow();
        int total = registroRepository.findByTitular(usuario).size();
        int pendientes = registroRepository.findByTitularAndEstado(usuario, EstadoRegistro.PENDIENTE).size();
        int vencidos = registroRepository
                .findByTitularAndFechaVencimientoBeforeAndEstado(usuario, LocalDate.now(), EstadoRegistro.PENDIENTE)
                .size();
        
        int pagados = registroRepository.findByTitularAndEstado(usuario, EstadoRegistro.PAGADO).size();

        // Calcular resumen de operaciones del mes actual
        LocalDate now = LocalDate.now();
        OperacionIGVDtos.ResumenIGV resumenMes = operacionIGVService.calcularResumenMensual(usuario, now.getMonthValue(), now.getYear());

        Map<String, Object> payload = new HashMap<>();
        payload.put("total", total);
        payload.put("pendientes", pendientes);
        payload.put("vencidos", vencidos);
        payload.put("pagados", pagados);
        
        // Nuevos campos para el dashboard
        payload.put("ventasMes", resumenMes.getTotalVentas());
        payload.put("comprasMes", resumenMes.getTotalCompras());
        payload.put("igvEstimado", resumenMes.getIgvAPagar());
        
        return payload;
    }

    @Override
    public void realizarPagoSimulado(String rut10, java.util.List<Long> registroIds, boolean pagarIgvMes) {
        Usuario usuario = usuarioRepository.findByRut10(rut10).orElseThrow();
        
        // 1. Pagar deudas seleccionadas (Registros)
        if (registroIds != null && !registroIds.isEmpty()) {
            var pendientes = registroRepository.findAllById(registroIds);
            pendientes.forEach(r -> {
                if (r.getTitular().getId().equals(usuario.getId()) && r.getEstado() == EstadoRegistro.PENDIENTE) {
                    r.setEstado(EstadoRegistro.PAGADO);
                }
            });
            registroRepository.saveAll(pendientes);
        }

        // 2. Generar registro de pago de IGV del mes actual si se solicitó
        if (pagarIgvMes) {
            LocalDate now = LocalDate.now();
            OperacionIGVDtos.ResumenIGV resumenMes = operacionIGVService.calcularResumenMensual(usuario, now.getMonthValue(), now.getYear());
            
            if (resumenMes.getIgvAPagar().compareTo(java.math.BigDecimal.ZERO) > 0) {
                com.devintegrado.impuestoscal.model.RegistroTributario pagoIgv = com.devintegrado.impuestoscal.model.RegistroTributario.builder()
                        .tipoImpuesto("IGV " + now.getMonth() + " " + now.getYear())
                        .monto(resumenMes.getIgvAPagar())
                        .fechaVencimiento(now.plusDays(15))
                        .estado(EstadoRegistro.PAGADO)
                        .titular(usuario)
                        .fechaCreacion(java.time.LocalDateTime.now())
                        .fechaActualizacion(java.time.LocalDateTime.now())
                        .activo(true)
                        .build();
                
                registroRepository.save(pagoIgv);
            }
        }
    }
}
