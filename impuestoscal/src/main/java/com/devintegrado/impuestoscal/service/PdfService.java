package com.devintegrado.impuestoscal.service;

import com.devintegrado.impuestoscal.model.Usuario;

public interface PdfService {
    byte[] generarDeclaracionMensual(String rut10, int mes, int anio);
}
