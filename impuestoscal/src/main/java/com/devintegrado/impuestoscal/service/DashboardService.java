package com.devintegrado.impuestoscal.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> obtenerResumen(String rut10);
    void realizarPagoSimulado(String rut10, List<Long> registroIds, boolean pagarIgvMes);
}
