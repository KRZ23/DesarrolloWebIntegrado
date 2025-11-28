package com.devintegrado.impuestoscal.service;

import com.devintegrado.impuestoscal.model.Usuario;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> obtenerResumen(String rut10);
}
