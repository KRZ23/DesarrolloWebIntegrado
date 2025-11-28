package com.devintegrado.impuestoscal.service.impl;

import com.devintegrado.impuestoscal.model.EstadoRegistro;
import com.devintegrado.impuestoscal.model.Usuario;
import com.devintegrado.impuestoscal.repository.RegistroTributarioRepository;
import com.devintegrado.impuestoscal.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final RegistroTributarioRepository registroRepository;
    private final com.devintegrado.impuestoscal.repository.UsuarioRepository usuarioRepository;

    public DashboardServiceImpl(RegistroTributarioRepository registroRepository, com.devintegrado.impuestoscal.repository.UsuarioRepository usuarioRepository) {
        this.registroRepository = registroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Map<String, Object> obtenerResumen(String rut10) {
        Usuario usuario = usuarioRepository.findByRut10(rut10).orElseThrow();
        int total = registroRepository.findByTitular(usuario).size();
        int pendientes = registroRepository.findByTitularAndEstado(usuario, EstadoRegistro.PENDIENTE).size();
        int vencidos = registroRepository
                .findByTitularAndFechaVencimientoBeforeAndEstado(usuario, LocalDate.now(), EstadoRegistro.PENDIENTE)
                .size();
        
        // Fix: Calculate pagados explicitly or derive correctly
        // If 'pendientes' includes 'vencidos', then 'pagados' = total - pendientes.
        // Alternatively, query for PAGADO.
        // Let's query for PAGADO to be safe and explicit.
        int pagados = registroRepository.findByTitularAndEstado(usuario, EstadoRegistro.PAGADO).size();

        Map<String, Object> payload = new HashMap<>();
        payload.put("total", total);
        payload.put("pendientes", pendientes);
        payload.put("vencidos", vencidos);
        payload.put("pagados", pagados); // New field
        return payload;
    }
}
