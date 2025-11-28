package com.devintegrado.impuestoscal.service.impl;

import com.devintegrado.impuestoscal.model.EstadoRegistro;
import com.devintegrado.impuestoscal.model.RegistroTributario;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.RegistroTributarioRepository;
import com.devintegrado.impuestoscal.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final RegistroTributarioRepository registroRepository;

    public DashboardServiceImpl(RegistroTributarioRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @Override
    public Map<String, Object> getResumen(Usuario usuario) {
        List<RegistroTributario> todos = registroRepository.findByTitular(usuario);

        // Total: Activos
        int total = (int) todos.stream().filter(RegistroTributario::getActivo).count();

        // Pendientes: Estado PENDIENTE
        int pendientes = (int) todos.stream()
                .filter(r -> r.getActivo() && r.getEstado() == EstadoRegistro.PENDIENTE)
                .count();

        // Vencidos: Estado PENDIENTE y Fecha < Hoy
        int vencidos = (int) todos.stream()
                .filter(r -> r.getActivo() && r.getEstado() == EstadoRegistro.PENDIENTE && r.getFechaVencimiento().isBefore(LocalDate.now()))
                .count();
        
        // Pagados: Estado PAGADO
        int pagados = (int) todos.stream()
                .filter(r -> r.getActivo() && r.getEstado() == EstadoRegistro.PAGADO)
                .count();

        Map<String, Object> payload = new HashMap<>();
        payload.put("total", total);
        payload.put("pendientes", pendientes);
        payload.put("vencidos", vencidos);
        payload.put("pagados", pagados);

        return payload;
    }
}
